package simternet.network;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.temporal.AsyncUpdate;

public abstract class AbstractNetwork implements AsyncUpdate, Steppable, Serializable {

	private static final long								serialVersionUID	= 1L;
	/**
	 * If there is no specific route, send traffic out this link. The link
	 * should still be in the list of egress links, along with the associated
	 * queue.
	 */
	protected BackboneLink									defaultRoute		= null;
	/**
	 * Contains the set of other networks we can send traffic to, and the
	 * associated backbone link.
	 */
	protected HashMap<AbstractNetwork, BackboneLink>		egressLinks			= new HashMap<AbstractNetwork, BackboneLink>();
	/**
	 * Contains the set of other networks we receive traffic from, and the
	 * associated backbone link.
	 */
	protected HashMap<AbstractNetwork, BackboneLink>		ingressLinks		= new HashMap<AbstractNetwork, BackboneLink>();

	/**
	 * Contains the full routing table of all networks we can reach, and a
	 * routing table entry (i.e., the next hop)
	 */
	protected HashMap<AbstractNetwork, RoutingTableEntry>	routingTable		= new HashMap<AbstractNetwork, RoutingTableEntry>();

	/**
	 * Accept ingress link requests from other networks. For now, this function
	 * should always be called by the createEgressLinkTo() function of the
	 * connecting network.
	 * 
	 * TODO: A provider's willingness to accept such links is subject to
	 * bargaining.
	 * 
	 * @param l
	 */
	public void acceptIngressLinkFrom(BackboneLink l) {
		this.ingressLinks.put(l.getSource(), l);
		// Send our initial routing table
		if (l.routingProtocolConfig != RoutingProtocolConfig.TRANSIT)
			for (RoutingTableEntry rte : this.routingTable.values())
				l.source.routingProtocolReceive(rte);
	}

	public void createEgressLinkTo(AbstractNetwork an, BackboneLink l) {
		this.createEgressLinkTo(an, l, RoutingProtocolConfig.NONE);
	}

	/**
	 * Create an egress link to network an. If the link is not specified, one
	 * with an infinite capacity will be created.
	 * 
	 * @param an
	 * @param l
	 */
	public void createEgressLinkTo(AbstractNetwork an, BackboneLink l, RoutingProtocolConfig config) {

		if (l == null) {
			l = new BackboneLink(this, an);
			l.makeInfinite();
		}

		// track this link
		this.egressLinks.put(an, l);

		// Now, set up routing...
		l.setRoutingProtocolConfig(config);

		// Send outselves a routing protocol update with a zero distance.
		// Routers always send to a connected network directly through
		// that interface.
		RoutingTableEntry rte = new RoutingTableEntry(an, l, 0);
		this.routingProtocolReceive(rte);

		// If we don't have a default route yet, use this link. The effect of
		// this procedure is that the default route is the first link added
		// unless it is changed later.
		if (this.defaultRoute == null)
			this.defaultRoute = l;

		an.acceptIngressLinkFrom(l);
	}

	public BackboneLink getDefaultRoute() {
		return this.defaultRoute;
	}

	// public abstract Double getBuildCost();

	/**
	 * @param to
	 *            The target network
	 * @return The associated backbone link, or null if not connected.
	 */
	public BackboneLink getEgressLink(AbstractNetwork to) {
		return this.egressLinks.get(to);
	}

	public String getRoutingTableReport() {
		StringBuffer sb = new StringBuffer();
		for (AbstractNetwork dest : this.routingTable.keySet())
			sb.append(dest + " -> " + this.routingTable.get(dest) + "\n");
		return sb.toString();
	}

	public boolean isConnectedTo(AbstractNetwork target) {
		boolean isConnected = false;
		if (this.egressLinks.containsKey(target))
			isConnected = true;
		if (this.ingressLinks.containsKey(target))
			isConnected = true;
		return isConnected;
	}

	public void noteCongestion(NetFlow flow) {
		// do nothing; should be used mostly by Datacenter networks.
	}

	/**
	 * This function is the main entry point for agents operating their
	 * networks. route() receives traffic from each ingress BackboneLink. It
	 * then makes a routing decision, sending the flow out the appropriate
	 * egress interface. Congestion is handled by each interface.
	 * 
	 * @author kkoning
	 */
	public void route() {
		// Iterate over all interfaces receiving flows
		for (BackboneLink link : this.ingressLinks.values()) {
			Collection<NetFlow> flows = link.receiveFlows();
			// Iterate over all flows received on the interface
			for (NetFlow flow : flows)
				this.route(flow);
		}
	}

	public void route(NetFlow flow) {
		// Send these flows out the correct egress links
		// Routing table lookup
		RoutingTableEntry rte = this.routingTable.get(flow.destination);
		BackboneLink outgoing;
		if (rte == null)
			outgoing = this.defaultRoute;
		else
			outgoing = rte.nextHop;

		if (outgoing != null)
			// place in appropriate output queue
			outgoing.sendFlow(flow);
		else
			System.out.println("No route for " + flow);
	}

	/**
	 * This function should (only) be called by another network which is sending
	 * us a routing protocol update. It should examine the update and decide
	 * whether to use the new route, based on whether the distance is smaller.
	 * If it does, it should pass the update along to its routing peers (after
	 * incrementing the distance by one).
	 * 
	 * @param update
	 */
	protected void routingProtocolReceive(RoutingTableEntry update) {
		Boolean accept = false;
		RoutingTableEntry currentRoute = this.routingTable.get(update.destination);
		if (currentRoute == null)
			accept = true;
		else if (currentRoute.distance > update.distance)
			accept = true;

		if (accept) {
			// add the route to our routing table
			this.routingTable.put(update.destination, update);
			// pass the update along to our neighbors
			this.routingProtocolSend(update);
		}
	}

	/**
	 * After altering our own routing table, we should send a routing protocol
	 * update to our neighbors informing them that the network is now reachable
	 * through us with a given distance (equivilence of AS path length). This
	 * function takes a single such update and distributes it to all other
	 * networks which send traffic to us. (i.e., back through links which are
	 * <b>ingress</b> to us, which means they will be <b>egress</b> to the far
	 * side.
	 * 
	 * @param update
	 */
	protected void routingProtocolSend(RoutingTableEntry update) {

		// Send updates to all networks which request them. In other
		// words, don't waste resources sending updates to stub networks
		// which would ignore them anyway.
		for (BackboneLink link : this.ingressLinks.values())
			// TODO: Peer Routes
			if (link.routingProtocolConfig != RoutingProtocolConfig.NONE) {

				// Create a new routing table entry, with this link as the next
				// hop
				// and increment the distance by one.
				RoutingTableEntry toTransmit = new RoutingTableEntry(update.destination, link, update.distance + 1);

				// Send the update
				link.source.routingProtocolReceive(toTransmit);
			}
	}

	public void setDefaultRoute(BackboneLink defaultRoute) {
		this.defaultRoute = defaultRoute;
	}

	/**
	 * Convience method to change the bandwidth available to an ISP this agent
	 * is already connected with.
	 * 
	 * @param anp
	 */
	public void setEgressBandwidth(AbstractNetwork an, Double bandwidth) {
		BackboneLink bl = this.getEgressLink(an);
		if (bl != null)
			bl.setBandwidth(bandwidth);
		else
			Logger.getRootLogger().log(Level.ERROR,
					this + " tried to set egress bandwidth to unconnected destination: " + an.toString());
	}

	/**
	 * Convience method to change the latency available to
	 * 
	 * @param anp
	 * @param latency
	 */
	public void setEgressLatency(AbstractNetwork an, Double latency) {
		BackboneLink bl = this.getEgressLink(an);
		if (bl != null)
			bl.setLatency(latency);
		else
			Logger.getRootLogger().log(Level.ERROR,
					this + " tried to set egress latency to unconnected destination: " + an.toString());
	}

	/*
	 * This function is responsible for top-level operational management of the
	 * network node.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {

		Logger.getRootLogger().log(Level.DEBUG, this + " Default Route: " + this.getDefaultRoute());
		Logger.getRootLogger().log(Level.DEBUG, this + " Routing Table:\n" + this.getRoutingTableReport());

		// Routing decisions are made first, analagous to a router "backplane"
		this.route();
		// Clear output queues on each egress interface.
		this.transmit();
	}

	/**
	 * Send out traffic waiting in output queues on all egress links.
	 */
	public void transmit() {
		for (BackboneLink link : this.egressLinks.values())
			link.transmit();
	}

	@Override
	public void update() {
		// this.egressLinks.update();
		// this.ingressLinks.update();
		// this.routingTable.update();
		// this.defaultRoute.update();
	}

}
