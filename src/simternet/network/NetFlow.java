package simternet.network;

import java.util.Comparator;

import simternet.TraceConfig;
import simternet.consumer.Consumer;

/**
 * TODO: Modify to require user, source, destination, at time of object
 * creation.
 * 
 * @author kkoning
 * 
 */
public abstract class NetFlow {

	//
	// FLOW ROUTING INFORMATION
	//

	public final Network		source;
	public final Network		destination;

	/**
	 * The user this traffic is intended for, once we reach the destination
	 * network. This is analagous to the host portion of a CIDR IPv4 address.
	 */
	protected final Consumer	user;

	public Integer				TTL			= 20;

	//
	// FLOW PERFORMANCE INFORMATION
	//

	/**
	 * The actual current bandwidth of this flow, which changes as it transits
	 * the network.
	 */
	public double				bandwidth;
	public final double			bandwidthRequested;

	/**
	 * Has this flow ever been congested?
	 */
	public boolean				congested	= false;

	/**
	 * The actual duration of this flow. For interactive flows, this should
	 * always be equal to maxTime. For non-interactive flows, this may be less
	 * because the flows will transfer as quickly as possible.
	 */
	public double				duration;
	public double				durationRequested;

	/**
	 * The accumulated latency of this flow
	 */
	public double				latency		= 0D;

	protected NetFlow(Network source, Network destination, Consumer user, double bandwidth, double duration) {

		// These checks are important for debugging to make sure that EA's
		// cannot exploit
		// odd behavior here.
		if (TraceConfig.sanityChecks) {
			if (source == null)
				throw new RuntimeException("Can't send a packet from nowhere");
			if (destination == null)
				throw new RuntimeException("Can't send a packet to nowhere");
			if (Double.isInfinite(bandwidth) || Double.isNaN(bandwidth) || bandwidth <= 0)
				throw new RuntimeException("Can't have nonsense bandwidth " + bandwidth);
			if (Double.isInfinite(duration) || Double.isNaN(duration) || duration <= 0)
				throw new RuntimeException("Can't have nonsense duration " + duration);
		}

		this.source = source;
		this.destination = destination;
		this.user = user;

		this.bandwidth = bandwidth;
		bandwidthRequested = bandwidth;
		this.duration = duration;
		durationRequested = duration;
	}

	/**
	 * Limit this flow based on the available network resources. While
	 * serialization latency is applied elsewhere, this function may
	 * additionally increase latency for highly congested links.
	 * 
	 * @param bandwidth
	 *            Restrict the flow to this maximum bandwidth
	 */
	public abstract void congest(double bandwidth);

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

	public abstract double getCongestionDuration();

	public double getActualTransfer() {
		double actualTransfer = bandwidth * duration;
		return actualTransfer;
	}

	public double getRequestedTransfer() {
		double requestedTransfer = bandwidthRequested * durationRequested;
		return requestedTransfer;
	}

	public double getBlockedTransfer() {
		double blockedTransfer = getRequestedTransfer() - getActualTransfer();
		return blockedTransfer;
	}

	public boolean isCongested() {
		return congested;
	}

	/**
	 * @return A human-readable interpretation of how this flow is congested,
	 *         e.g., 5/10Mbps.
	 */
	public String describeCongestionForHumans() {
		StringBuffer sb = new StringBuffer();
		sb.append("BW=");
		sb.append(bandwidth);
		sb.append("/");
		sb.append(bandwidthRequested);
		sb.append(",DUR=");
		sb.append(duration);
		sb.append("/");
		sb.append(durationRequested);
		return sb.toString();
	}

	@Override
	public String toString() {
		return "Flow: " + source + " -> " + user + "@" + destination + ", " + describeCongestionForHumans();
	}

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

}
