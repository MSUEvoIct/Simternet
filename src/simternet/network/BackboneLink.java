package simternet.network;

import java.util.ArrayList;
import java.util.List;

/**
 * A BackboneLink is a simplex connection between two Networks. BackboneLinks
 * are roughly analagous to router 'interfaces'. While its endpoints cannot be
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
public class BackboneLink {

	public static BackboneLink createSymmetricLink(BackboneLink a) {
		BackboneLink b = new BackboneLink(a.source, a.destination);
		b.setBandwidth(a.getBandwidth());
		b.setLatency(a.getLatency());
		return b;
	}

	/**
	 * The number of data units this link may transfer each model time step.
	 */
	protected Double bandwidth;

	/**
	 * A provider may choose a congestion algorithm, or QoS policy, on a
	 * per-link basis.
	 * 
	 * TODO: For now, use the Internet 'best effort' approximating
	 * WFQCongestionAlgorithm();
	 */
	protected CongestionAlgorithm congestionAlgorithm = new WFQCongestionAlgorithm(
			this);

	/**
	 * When traffic is sent through this link, it winds up at this network.
	 */
	protected final AbstractNetwork destination;

	/**
	 * This is the set of network flows which the source/transmittion network
	 * would like to send using this link. They are not transmitted (placed in
	 * the outputQueue) until processing by this link's congestion algorithm.
	 */
	protected List<NetFlow> inputQueue = new ArrayList<NetFlow>();

	/**
	 * The link adds this amount of latency, or delay, to each flow which it
	 * transmits.
	 */
	protected Double latency;

	/**
	 * This is the set of network flows which are ready to be received by the
	 * destination network.
	 */
	protected List<NetFlow> outputQueue = new ArrayList<NetFlow>();

	/**
	 * Controls what routing protocol information is sent to the network which
	 * uses this link as egress. By default, no information is sent.
	 */
	protected RoutingProtocolConfig routingProtocolConfig = RoutingProtocolConfig.NONE;

	/**
	 * The transmitting network.
	 */
	protected final AbstractNetwork source;

	public BackboneLink(final AbstractNetwork source,
			final AbstractNetwork destination) {
		this.source = source;
		this.destination = destination;
	}

	public Double getBandwidth() {
		return this.bandwidth;
	}

	public AbstractNetwork getDestination() {
		return this.destination;
	}

	public Double getLatency() {
		return this.latency;
	}

	public RoutingProtocolConfig getRoutingProtocolConfig() {
		return this.routingProtocolConfig;
	}

	public AbstractNetwork getSource() {
		return this.source;
	}

	public void makeInfinite() {
		this.latency = 0D;
		// this.bandwidth = Double.MAX_VALUE;
		this.bandwidth = 5.0E7;

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
		List<NetFlow> toReturn = this.outputQueue;
		this.outputQueue = new ArrayList<NetFlow>();
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
		this.inputQueue.add(flow);
	}

	public void setBandwidth(Double bandwidth) {
		this.bandwidth = bandwidth;
	}

	public void setLatency(Double latency) {
		this.latency = latency;
	}

	public void setRoutingProtocolConfig(
			RoutingProtocolConfig routingProtocolConfig) {
		this.routingProtocolConfig = routingProtocolConfig;
	}

	@Override
	public String toString() {
		return "Link " + this.getSource() + "->" + this.getDestination()
				+ ", BW=" + this.bandwidth;
	}

	/**
	 * Called by the source network once all flows for this time step are in the
	 * queue. These flows are then processed by the congestion algorithm and
	 * placed in the output queue to be retrieved by the target network.
	 */
	public void transmit() {
		for (NetFlow f : this.inputQueue)
			f.latency += this.latency;

		this.outputQueue.addAll(this.congestionAlgorithm.limit(this.inputQueue,
				this));

		this.inputQueue.clear();

	}

}
