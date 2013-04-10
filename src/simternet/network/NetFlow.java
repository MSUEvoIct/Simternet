package simternet.network;

import java.util.Comparator;

import simternet.TraceConfig;

/**
 * TODO: Modify to require user, source, destination, at time of object
 * creation.
 * 
 * @author kkoning
 * 
 */
public class NetFlow {

	//
	// FLOW ROUTING INFORMATION
	//
	public byte aspID;
	public final Network source;
	public final Network destination;

	// Just a counter to prevent infinite loops of network traffic.
	public int TTL = 7;

	//
	// FLOW PERFORMANCE INFORMATION
	//

	/**
	 * The actual current bandwidth of this flow, which changes as it transits
	 * the network.
	 */
	public float bandwidth;
	public final float bandwidthRequested;

	protected NetFlow(byte aspID, Network source, Network destination,
			float bandwidth) {

		// These checks are important for debugging to make sure that EA's
		// cannot exploit
		// odd behavior here.
		if (TraceConfig.sanityChecks) {
			if (source == null)
				throw new RuntimeException("Can't send a packet from nowhere");
			if (destination == null)
				throw new RuntimeException("Can't send a packet to nowhere");
			if (Double.isInfinite(bandwidth) || Double.isNaN(bandwidth)
					|| bandwidth <= 0)
				throw new RuntimeException("Can't have nonsense bandwidth "
						+ bandwidth);
		}

		this.aspID = aspID;
		this.source = source;
		this.destination = destination;

		this.bandwidth = bandwidth;
		this.bandwidthRequested = bandwidth;
	}

	/**
	 * Limit this flow based on the available network resources. While
	 * serialization latency is applied elsewhere, this function may
	 * additionally increase latency for highly congested links.
	 * 
	 * @param bandwidth
	 *            Restrict the flow to this maximum bandwidth
	 */
	public void congest(float bandwidth) {
		if (this.bandwidth > bandwidth) {
			this.bandwidth = bandwidth;
		}
	}

	// /**
	// *
	// * @return The ratio of actual transfer over transfer requested. If
	// transfer
	// * requested is zero, return 1.
	// */
	// public double getTransferFraction() {
	// double requestedTransfer = getRequestedTransfer();
	// double transferFraction = 0D;
	// if (requestedTransfer == 0D)
	// return 1;
	// else {
	// transferFraction = getActualTransfer() / requestedTransfer;
	// }
	//
	// return transferFraction;
	// }

	// public double getBlockedTransfer() {
	// double blockedTransfer = getRequestedTransfer() - getActualTransfer();
	// return blockedTransfer;
	// }

	// public boolean isCongested() {
	// return congested;
	// }

	public float congestionRatio() {
		float percentThrough = bandwidth / bandwidthRequested;
		float percentCongested = 1 - percentThrough;
		return percentCongested;
	}

	@Override
	public String toString() {
		return "Flow: " + source + " -> " + destination + ", BW=" + bandwidth
				+ "/" + bandwidthRequested;
	}

	/**
	 * This comparator allows for sorting of NetFlow lists in increasing order
	 * by <i>interactiveBandwidth</i>. This is primarily used by the WFQ
	 * congestion algorithm.
	 * 
	 * @author kkoning
	 * 
	 */
	public static class CongestionBandwidthComparator implements
			Comparator<NetFlow> {
		@Override
		public int compare(NetFlow o1, NetFlow o2) {
			if (o1.bandwidth > o2.bandwidth)
				return 1;
			if (o1.bandwidth < o2.bandwidth)
				return -1;
			return 0;
		}
	}

}
