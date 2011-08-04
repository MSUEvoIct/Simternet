package simternet.network;

import java.util.Comparator;

import simternet.consumer.Consumer;

/**
 * TODO: Modify to require user, source, destination, at time of object
 * creation.
 * 
 * @author kkoning
 * 
 */
public abstract class NetFlow {

	/**
	 * This comparator allows for sorting of NetFlow lists in increasing order
	 * by <i>interactiveBandwidth</i>. This is primarily used by the WFQ
	 * congestion algorithm.
	 * 
	 * @author kkoning
	 * 
	 */
	public static class CongestionBandwidthComparator implements Comparator<NetFlow> {
		@Override
		public int compare(NetFlow o1, NetFlow o2) {
			if (o1.getCongestionBandwidth() > o2.getCongestionBandwidth())
				return 1;
			if (o1.getCongestionBandwidth() < o2.getCongestionBandwidth())
				return -1;
			return 0;
		}
	}

	/**
	 * The actual current bandwidth of this flow, which changes as it transits
	 * the network.
	 */
	protected Double			bandwidth;

	/**
	 * Has this flow ever been congested?
	 */
	protected Boolean			congested	= false;

	/**
	 * The network this NetFlow object will be delivered to.
	 */
	protected final Network		destination;
	/**
	 * The actual duration of this flow. For interactive flows, this should
	 * always be equal to maxTime. For non-interactive flows, this may be less
	 * because the flows will transfer as quickly as possible.
	 */
	protected Double			duration;

	/**
	 * The accumulated latency of this flow
	 */
	protected Double			latency		= 0D;

	/**
	 * The source network, should be a RoutingPoint operated by an application
	 * provider.
	 */
	protected final Network		source;

	protected final Double		transferRequested;

	/**
	 * Exactly analogous to TTL in real networks. We should never have a network
	 * larger than 20 hops. This has not been placed in an external parameter
	 * because it's more of a debug/sanity check than anything else. We should
	 * probably quit with an error if this ever reaches zero.
	 */
	protected Integer			TTL			= 20;

	/**
	 * The user this traffic is intended for, once we reach the destination
	 * network. This is analagous to the host portion of a CIDR IPv4 address.
	 */
	protected final Consumer	user;

	protected NetFlow(Network source, Network destination, Consumer user, Double transferRequested) {
		if (destination == null)
			throw new RuntimeException("Can't send a packet to nowhere");
		this.source = source;
		this.destination = destination;
		this.user = user;
		this.transferRequested = transferRequested;
		// DEBUG
		if (this.transferRequested.isInfinite() || this.transferRequested.isNaN())
			throw new RuntimeException();
	}

	/**
	 * Limit this flow based on the available network resources. While
	 * serialization latency is applied elsewhere, this function may
	 * additionally increase latency for highly congested links.
	 * 
	 * @param bandwidth
	 *            Restrict the flow to this maximum bandwidth
	 */
	public abstract void congest(Double bandwidth);

	/**
	 * @return A human-readable interpretation of how this flow is congested,
	 *         e.g., 5/10Mbps.
	 */
	public abstract String describeCongestion();

	/**
	 * Non-interactive flows will not reduce their total usage unless the
	 * bandwidth falls below the point where its maximum duration is exceeded.
	 * Interactive flows will reduce their bandwidth consumption immediately. We
	 * need to know what this level is for all types of flows before we can
	 * execute a WFQ-like congestion algorithm.
	 * 
	 * @return The bandwidth at which this flow will reduce its usage.
	 */
	public abstract Double getCongestionBandwidth();

	/**
	 * We need to be able to calculate total usage remaining if limiting to a
	 * given bandwidth. For non-interactive flows, they won't last any longer
	 * than this.
	 * 
	 * @return The maximum
	 */
	public abstract Double getCongestionDuration();

	public Double getTransferActual() {
		double transferActual = this.bandwidth * this.duration * this.user.getPopulation();
		// DEBUG
		if (transferActual < 0)
			throw new RuntimeException();
		return transferActual;
	}

	public Double getTransferBlocked() {
		return this.getTransferRequested() - this.getTransferActual();
	}

	public Double getTransferRequested() {
		return this.transferRequested;
	}

	public Boolean isCongested() {
		return this.congested;
	}

	@Override
	public String toString() {
		return "Flow: " + this.source + " -> " + this.user + "@" + this.destination + ", " + this.duration + "s@"
				+ this.bandwidth + "b/s";
	}

}
