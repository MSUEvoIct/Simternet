package simternet.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simternet.Simternet;
import simternet.TraceConfig;

/**
 * A BackboneLink is a simplex connection between two Networks. BackboneLinks
 * are roughly analogous to router 'interfaces'. While its end-points cannot be
 * modified once this object has been instantiated (they are marked 'final'),
 * its bandwidth, latency, and congestion properties can.
 * 
 * Each BackboneLink has two queues for network flows (NetFlow objects), an
 * input queue and an output queue. The source network must add all the flows
 * which it will transmit through this link using the sendFlow() method. Once
 * all such flows have been added, the source network should call the
 * BackboneLink's transmit() function. This function will modify and move these
 * flows into the outputQueue, based on this link's available bandwidth
 * resources.
 * 
 * The receiving network should poll each of its ingress interfaces using
 * receiveFlows() at each time step.
 * 
 * @author kkoning
 * 
 */
public class BackboneLink implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * The number of data units this link may transfer each model time step.
	 */
	protected Double				bandwidth				= Double.MAX_VALUE;

	/**
	 * A provider may choose a congestion algorithm, or QoS policy, on a
	 * per-link basis.
	 * 
	 * TODO: For now, use the Internet 'best effort' approximating
	 * WFQCongestionAlgorithm();
	 */
	protected CongestionAlgorithm	congestionAlgorithm		= new WFQCongestionAlgorithm(this);

	/**
	 * When traffic is sent through this link, it winds up at this network.
	 */
	protected final Network			destination;

	/**
	 * The link adds this amount of latency, or delay, to each flow which it
	 * transmits, in addition to whatever latency may be added due to queuing.
	 */
	private Double					inherentLatency			= 0.0;

	/**
	 * This is the set of network flows which the source/transmission network
	 * would like to send using this link. They are not transmitted (placed in
	 * the outputQueue) until processing by this link's congestion algorithm. It
	 * is analogous to the output queue of the transmitting router for this
	 * interface.
	 */
	protected List<NetFlow>			inputQueue				= new ArrayList<NetFlow>();

	/**
	 * This is the set of network flows which are ready to be received by the
	 * destination network. It represents the packets actually transmitted by
	 * the source router.
	 */
	protected List<NetFlow>			outputQueue				= new ArrayList<NetFlow>();

	/**
	 * Controls what routing protocol information is sent to the network which
	 * uses this link as egress. By default, no information is sent.
	 */
	protected RoutingProtocolConfig	routingProtocolConfig	= RoutingProtocolConfig.NONE;

	Map<Network, Route>				routingTable;

	/**
	 * The transmitting network.
	 */
	protected final Network			source;

	public double					perStepDemand			= 0D;
	public double					perStepTransmitted		= 0D;
	public double					totalDemand				= 0D;
	public double					totalTransmitted		= 0D;
	// per step capacity is bandwidth. bw may change, per step...
	public double					totalCapacity			= 0D;

	/**
	 * Create a backbone link, automatically add it to the source and
	 * destination networks as egress and ingress links respectively.
	 * 
	 * @param source
	 * @param destination
	 * @param bandwidth
	 *            The link's bandwidth, or Double.MAX_VALUE if null.
	 */
	public BackboneLink(final Network source, final Network destination, Double bandwidth) {
		this.source = source;
		this.destination = destination;
		if (bandwidth != null) {
			this.bandwidth = bandwidth;
		}

		this.source.egressLinks.put(destination, this);
		this.destination.ingressLinks.put(source, this);

		initRoutingTable();
	}

	public void disconnect() {
		stopRoutingProtocol();
		destination.ingressLinks.remove(source);
		source.egressLinks.remove(destination);
	}



	private void initRoutingTable() {
		routingTable = new HashMap<Network, Route>();
		Route directlyConnected = new Route(destination, this, 0);
		routingTable.put(destination, directlyConnected);
		source.receiveRoute(directlyConnected);
	}

	public void makeInfinite() {
		inherentLatency = 0D;
		this.bandwidth = Double.MAX_VALUE;
	}

	/**
	 * This function should only be called by the destination network, when it
	 * is ready to process the incoming traffic from this link. Once the
	 * destination network accepts this traffic, the output queue is cleared.
	 * 
	 * @return The list of processed (post-congestion) flows to be received by
	 *         the destination network.
	 */
	public List<NetFlow> receiveFlows() {
		List<NetFlow> toReturn = outputQueue;
		outputQueue = new ArrayList<NetFlow>();
		return toReturn;

	}

	/**
	 * This function should be called by the transmitting network as part of its
	 * routing process. All flows which are to be routed through this link are
	 * placed in the inputQueue until they can be processed by the congestion
	 * algorithm.
	 * 
	 * @param flow
	 */
	public void sendFlow(NetFlow flow) {
		inputQueue.add(flow);
	}

	public void setBandwidth(Double bandwidth) {
		if (bandwidth < Double.MIN_NORMAL) {
			this.bandwidth = Double.MIN_NORMAL;
		} else {
			this.bandwidth = bandwidth;
		}
	}

	public void setCongestionAlgorithm(CongestionAlgorithm congestionAlgorithm) {
		this.congestionAlgorithm = congestionAlgorithm;
	}

	public void setLatency(Double latency) {
		inherentLatency = latency;
	}

	public void setRoutingProtocolConfig(RoutingProtocolConfig routingProtocolConfig) {
		if (this.routingProtocolConfig.equals(routingProtocolConfig))
			return; // no change, do nothing

		if (routingProtocolConfig.equals(RoutingProtocolConfig.NONE)) {
			stopRoutingProtocol(); // was something, now nothing
			this.routingProtocolConfig = RoutingProtocolConfig.NONE;
			return;
		}

		// Otherwise, we're changing from none to some, or between them. so
		// reset
		stopRoutingProtocol();
		startRoutingProtocol();
		this.routingProtocolConfig = routingProtocolConfig;
	}

	private void startRoutingProtocol() {
		// start with a fresh new routing table
		initRoutingTable();

		// the destination network tells the sending network which
		// other networks it can reach through this link.
		destination.initRoutes(this);
	}

	private void stopRoutingProtocol() {
		// withdraw all routes
		for (Route route : routingTable.values()) {
			route.distance = Integer.MAX_VALUE;
			source.receiveRoute(route);
		}

		// wipe routing table
		initRoutingTable();
	}

	@Override
	public String toString() {
		return "Link:" + getSource() + "->" + getDestination() + ",B=" + Simternet.nf.format(bandwidth) + ",R="
				+ routingProtocolConfig.toString().charAt(0) + ",C=" + Simternet.nf.format(perStepCongestionRatio());
	}

	/**
	 * Called by the source network once all flows for this time step are in the
	 * queue. These flows are then processed by the congestion algorithm and
	 * placed in the input queue to be retrieved by the target network.
	 */
	public void transmitFlows() {
		totalCapacity += bandwidth;
		int numFlows = 0;

		perStepDemand = 0D;
		for (NetFlow f : inputQueue) {
			perStepDemand += f.bandwidth;
		}
		totalDemand += perStepDemand;

		// Congest the flows, get new list of congested flows
		Collection<NetFlow> congestedFlows = congestionAlgorithm.limit(inputQueue, this);

		perStepTransmitted = 0D;
		for (NetFlow f : congestedFlows) {
			perStepTransmitted += f.bandwidth;
			numFlows++;
		}
		totalTransmitted += perStepTransmitted;

		if (TraceConfig.networking.flowSent) {
			TraceConfig.out.println(this + " transmitting " + numFlows + " flows, with aggregate bw="
					+ perStepTransmitted);
			for (NetFlow f : congestedFlows) {
				TraceConfig.out.println(this + " transmitted " + f);

			}
		}

		// put congested flows in output queue and clear input queue
		outputQueue.addAll(congestedFlows);
		inputQueue.clear();
	}

	/**
	 * Called by the destination network when telling its peer (the source) that
	 * a particular network may be reached via this link. The default
	 * implementation keeps track of the route in a hash specifically for this
	 * link, and immediately passes it on to the destination network
	 * 
	 * This function is intended to recieve routes <i>directly</i> from the
	 * destination network's routing table. It takes care of cloning the routes,
	 * incrementing the distance, etc...
	 * 
	 * @param route
	 */
	public void updateRoute(Route route) {

		boolean beingWithdrawn = false;

		// if we're being told the route is being withdrawn
		if (route.distance == Integer.MAX_VALUE) {
			beingWithdrawn = true;
		}

		// We need a new copy of the route, as we're going to have our own
		// distance, path, etc...
		Route newRoute;
		try {
			newRoute = route.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		// This link is obviously the next hop now
		newRoute.setNextHop(this);

		// Track the network path (i.e., AS-PATH) for loop detection
		newRoute.path.add(destination);

		if (!beingWithdrawn) {
			// By default, increase the distance by 1
			newRoute.setDistance(route.getDistance() + 1);

			// If we're going to reach the destination network via this link,
			// we're going to do so using this route
			routingTable.put(route.destination, newRoute);
		} else {
			newRoute.setDistance(Integer.MAX_VALUE);
			routingTable.remove(route.destination);
		}

		// The network using this link to send (the source) needs to process the
		// update
		source.receiveRoute(newRoute);
	}

	public double totalCongestionRatio() {
		double fracServed = totalTransmitted / totalDemand;
		return 1 - fracServed;
	}

	public double perStepCongestionRatio() {
		double fracServed = perStepTransmitted / perStepDemand;
		return 1 - fracServed;
	}

	public Double getBandwidth() {
		return bandwidth;
	}

	public CongestionAlgorithm getCongestionAlgorithm() {
		return congestionAlgorithm;
	}

	public Network getDestination() {
		return destination;
	}

	public Double getLatency() {
		return inherentLatency;
	}

	public RoutingProtocolConfig getRoutingProtocolConfig() {
		return routingProtocolConfig;
	}

	public Network getSource() {
		return source;
	}
	
	
}
