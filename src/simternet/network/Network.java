package simternet.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.engine.TraceConfig;
import simternet.engine.asyncdata.AsyncUpdate;

/**
 * Abstract Network, the base Vertex/Node in the Simternet graph/Internetwork
 * 
 * @author kkoning
 * 
 */
public abstract class Network implements AsyncUpdate, Steppable, Serializable {

	private static final long				serialVersionUID	= 1L;

	/**
	 * If there is no specific route, send traffic out this link. The link
	 * should still be in the list of egress links, along with the associated
	 * queue.
	 */
	protected BackboneLink					defaultRoute		= null;

	/**
	 * Contains the set of other networks we can send traffic to, and the
	 * associated backbone link.
	 */
	public HashMap<Network, BackboneLink>	egressLinks			= new HashMap<Network, BackboneLink>();

	/**
	 * Contains the set of other networks we receive traffic from, and the
	 * associated backbone link.
	 */
	public HashMap<Network, BackboneLink>	ingressLinks		= new HashMap<Network, BackboneLink>();

	protected RoutePreference				routePreference		= new RoutePreference();

	/**
	 * Contains the full routing table of all networks we can reach, and a
	 * routing table entry (i.e., the next hop)
	 */
	protected HashMap<Network, Route>		routingTable		= new HashMap<Network, Route>();

	// /**
	// * Accept ingress link requests from other networks. For now, this
	// function
	// * should always be called by the createEgressLinkTo() function of the
	// * connecting network.
	// *
	// * TODO: A provider's willingness to accept such links is subject to
	// * bargaining.
	// *
	// * @param l
	// */
	// public void acceptIngressLinkFrom(BackboneLink l) {
	// this.ingressLinks.put(l.getSource(), l);
	// // Send our initial routing table
	// if (l.routingProtocolConfig != RoutingProtocolConfig.TRANSIT)
	// for (Route rte : this.routingTable.values())
	// l.source.routingProtocolReceive(rte);
	// }

	/**
	 * Create a link from this network to toNetwork.
	 * 
	 * @param toNetwork
	 * @param bandwidth
	 * @param config
	 */
	public void createEgressLinkTo(Network toNetwork, Double bandwidth, RoutingProtocolConfig config) {

		// merely by creation this link will be in this.egressLinks and
		// toNetwork.ingressLinks
		BackboneLink link = new BackboneLink(this, toNetwork, bandwidth);

		// set the routing protocol config, which initializes routing if
		// required
		link.setRoutingProtocolConfig(config);

		// If we don't have a default route yet, use this link. The effect of
		// this procedure is that the default route is the first link added
		// unless it is changed later.
		if (defaultRoute == null) {
			defaultRoute = link;
		}
	}

	/**
	 * This network is being destroyed, probably because its owner went
	 * bankrupt. Remove this network from the Simternet.
	 */
	public void disconnect() {
		// routing protocol updates handled by links themselves

		Vector<BackboneLink> linksToDisconnect = new Vector<BackboneLink>();

		// disconnect all my outgoing connections
		for (BackboneLink connection : egressLinks.values()) {
			linksToDisconnect.add(connection);
		}
		// disconnect all my incoming connections.
		for (BackboneLink connection : ingressLinks.values()) {
			linksToDisconnect.add(connection);
		}

		for (BackboneLink link : linksToDisconnect) {
			link.disconnect();
		}
	}

	/**
	 * Finds the best route to the destination network. This function is
	 * intended to be used in creating the routing table, not when actually
	 * routing traffic.
	 * 
	 * Default implementation prefers the shortest path. If there is a tie, it
	 * resolves to the highest bandwidth. If there's still a tie, just use the
	 * one that came up first.
	 * 
	 * @param destination
	 * @return
	 */
	Route getBestRoute(Network destination) {
		Route best = null;

		// Query the routes available to us from peers
		for (BackboneLink link : egressLinks.values()) {
			Route candidate = link.routingTable.get(destination);
			if (candidate == null) {
				continue; // so do nothing
			}

			if (best == null) {
				best = candidate;
				continue;
			}

			if (routePreference.compareRoutes(best, candidate) == RoutePreference.TWO_BETTER) {
				best = candidate;
			}
		}

		return best;
	}

	public BackboneLink getDefaultRoute() {
		return defaultRoute;
	}

	/**
	 * @param to
	 *            The target network
	 * @return The associated backbone link, or null if not connected.
	 */
	public BackboneLink getEgressLink(Network to) {
		return egressLinks.get(to);
	}

	public Collection<Network> getEgressPeers() {
		Collection<Network> peers = new ArrayList<Network>();
		for (Network n : egressLinks.keySet())
			if (egressLinks.get(n) != null) {
				peers.add(n);
			}
		return peers;
	}

	public Collection<Network> getIngressPeers() {
		Collection<Network> peers = new ArrayList<Network>();
		for (Network n : ingressLinks.keySet())
			if (ingressLinks.get(n) != null) {
				peers.add(n);
			}
		return peers;
	}

	public Collection<Network> getPeers() {
		Collection<Network> peers = new ArrayList<Network>();
		peers.addAll(getEgressPeers());
		peers.addAll(getIngressPeers());
		return peers;
	}

	/**
	 * Initialize the routing protocol information on this link. The default
	 * implementation sends all active entries in our routing table to the
	 * specified link, if they should be sent under that link's
	 * RoutingProtocolConfig.
	 * 
	 * @param link
	 * @param config
	 */
	protected void initRoutes(BackboneLink link) {
		for (Route route : routingTable.values())
			if (routePolicy(link.routingProtocolConfig, route)) {
				link.updateRoute(route);
			}
	}

	public boolean isConnectedTo(Network target) {
		boolean isConnected = false;
		if (egressLinks.containsKey(target)) {
			isConnected = true;
		}
		if (ingressLinks.containsKey(target)) {
			isConnected = true;
		}
		return isConnected;
	}

	public void noteCongestion(NetFlow flow) {
		// do nothing; should be used mostly by Datacenter networks.
	}

	/**
	 * Called when receiving a route from a peer. It should be called by the
	 * BackboneLink that is the egress link from this network to the peer
	 * network.
	 * 
	 * @param route
	 */
	void receiveRoute(Route route) {
		Route existingRoute = routingTable.get(route.destination);

		// if no route exists, use this one, unless it has max distance, in
		// which case it should be ignored.
		if (existingRoute == null) {
			if (route.distance < Integer.MAX_VALUE) {
				updateRoutingTable(route);
			}
			return;
		}

		// Otherwise, compare the routes
		Integer preference = routePreference.compareRoutes(existingRoute, route);
		Boolean updateActiveRoute;

		if (existingRoute.nextHop == route.nextHop) {
			updateActiveRoute = true;
		} else {
			updateActiveRoute = false;
		}

		// If the new route is better, use that instead
		if (preference == RoutePreference.TWO_BETTER) {
			updateRoutingTable(route);
			return;
		}

		// If the new route is worse...
		if (preference == RoutePreference.ONE_BETTER) {
			/*
			 * if it is on a different interface, it can be safely ignored. It
			 * will remain in the interface's route list if we need it later
			 */
			if (!updateActiveRoute)
				return;

			/*
			 * our active route just got worse. If we have a better one
			 * available in another interface, use that. otherwise, continue to
			 * use this, but update the routing table (and let peers know, they
			 * might have a better route) Since we're going to send to peers
			 * anyway, just find our best route and send that.
			 */
			Route newRoute = getBestRoute(route.destination);
			updateRoutingTable(newRoute);
			return;
		}

		/*
		 * If we got here, the routes have the same preference, so just continue
		 * to use our current one.
		 */
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
		for (BackboneLink link : ingressLinks.values()) {
			Collection<NetFlow> flows = link.receiveFlows();
			// Iterate over all flows received on the interface
			for (NetFlow flow : flows) {
				this.route(flow);
			}
		}
	}

	// /**
	// * This function should (only) be called by another network which is
	// sending
	// * us a routing protocol update. It should examine the update and decide
	// * whether to use the new route, based on whether the distance is smaller.
	// * If it does, it should pass the update along to its routing peers (after
	// * incrementing the distance by one).
	// *
	// * @param update
	// */
	// protected void routingProtocolReceive(Route update) {
	// Boolean accept = false;
	// Route currentRoute = this.routingTable.get(update.destination);
	// if (currentRoute == null)
	// accept = true;
	// else if (currentRoute.distance > update.distance)
	// accept = true;
	//
	// if (accept) {
	// // add the route to our routing table
	// this.routingTable.put(update.destination, update);
	// // pass the update along to our neighbors
	// this.routingProtocolSend(update);
	// }
	// }

	// /**
	// * After altering our own routing table, we should send a routing protocol
	// * update to our neighbors informing them that the network is now
	// reachable
	// * through us with a given distance (equivilence of AS path length). This
	// * function takes a single such update and distributes it to all other
	// * networks which send traffic to us. (i.e., back through links which are
	// * <b>ingress</b> to us, which means they will be <b>egress</b> to the far
	// * side.
	// *
	// * @param update
	// */
	// protected void routingProtocolSend(Route update) {
	//
	// // Send updates to all networks which request them. In other
	// // words, don't waste resources sending updates to stub networks
	// // which would ignore them anyway.
	// for (BackboneLink link : this.ingressLinks.values())
	// // TODO: Peer Routes
	// if (link.routingProtocolConfig != RoutingProtocolConfig.NONE) {
	//
	// // Create a new routing table entry, with this link as the next
	// // hop and increment the distance by one.
	// Route toTransmit = new Route(update.destination, link, update.distance +
	// 1);
	//
	// // Send the update
	// link.source.routingProtocolReceive(toTransmit);
	// }
	// }

	public void route(NetFlow flow) {
		// Send these flows out the correct egress links

		// Routing table lookup
		Route rte = routingTable.get(flow.destination);
		BackboneLink outgoing;
		if (rte == null) {
			outgoing = defaultRoute;
		} else {
			outgoing = rte.nextHop;
		}

		if (TraceConfig.networking.routingDecisions) {
			TraceConfig.out.println("Routing " + flow + " to outgoing Link = " + outgoing);
		}

		if (outgoing != null) {
			// place in appropriate output queue
			outgoing.sendFlow(flow);
		} else {
			System.out.println("No route for " + flow);
		}
	}

	protected boolean routePolicy(RoutingProtocolConfig config, Route route) {
		boolean shouldBeSent = false;

		if (config == RoutingProtocolConfig.TRANSIT) {
			shouldBeSent = true;
		}

		if (config == RoutingProtocolConfig.PEER)
			if (route.distance <= 1) {
				shouldBeSent = true;
			}

		if (route.distance == 0) {
			shouldBeSent = true;
		}

		return shouldBeSent;
	}

	public String routingTableReport() {
		StringBuffer sb = new StringBuffer();
		for (Network dest : routingTable.keySet()) {
			sb.append(dest + " -> " + routingTable.get(dest) + "\n");
		}
		sb.append("Default -> " + defaultRoute + "\n");
		return sb.toString();
	}

	protected void sendRouteToNeighbors(Route route) {
		for (BackboneLink link : ingressLinks.values())
			if (routePolicy(link.routingProtocolConfig, route)) {
				link.updateRoute(route);
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
	public void setEgressBandwidth(Network an, Double bandwidth) {
		BackboneLink bl = getEgressLink(an);
		if (bl != null) {
			bl.setBandwidth(bandwidth);
		} else {
			Logger.getRootLogger().log(Level.ERROR,
					this + " tried to set egress bandwidth to unconnected destination: " + an.toString());
		}
	}

	/**
	 * Convience method to change the latency available to
	 * 
	 * @param anp
	 * @param latency
	 */
	public void setEgressLatency(Network an, Double latency) {
		BackboneLink bl = getEgressLink(an);
		if (bl != null) {
			bl.setLatency(latency);
		} else {
			Logger.getRootLogger().log(Level.ERROR,
					this + " tried to set egress latency to unconnected destination: " + an.toString());
		}
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

		if (TraceConfig.networking.routingTables && Logger.getRootLogger().isTraceEnabled()) {
			Logger.getRootLogger().trace(this + " Default Route: " + getDefaultRoute());
			Logger.getRootLogger().trace(this + " Routing Table:\n" + routingTableReport());
		}

		// Routing decisions are made first, analagous to a router "backplane"
		this.route();
		// Clear output queues on each egress interface.
		transmitFlows();
	}

	/**
	 * Send out traffic waiting in output queues on all egress links.
	 */
	public void transmitFlows() {
		for (BackboneLink link : egressLinks.values()) {
			link.transmitFlows();
		}
	}

	@Override
	public void update() {
		/*
		 * By default, Networks don't have any Temporal objects that need
		 * updating. Temporal structures in network structure create fundamental
		 * inconsistencies that are a PITA to deal with. Don't change this
		 * unless you've thought about it, decided it's necessary, and make sure
		 * things still work properly!
		 */
	}

	/**
	 * The specified route has been chosen for inclusion in the routing table.
	 * This function should actually make the insertion and notify all of our
	 * neighbors.
	 * 
	 * @param route
	 */
	protected void updateRoutingTable(Route route) {
		try {
			// cloned so that if route gets modified in interface it doesn't
			// changed here as well
			Route myRoute = route.clone();
			routingTable.put(route.destination, route);
			sendRouteToNeighbors(myRoute);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
