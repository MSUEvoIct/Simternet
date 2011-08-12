package simternet.network;

import java.io.Serializable;

import simternet.consumer.Consumer;

public class UserInteractiveFlow extends NetFlow implements Serializable {

	private static final long	serialVersionUID	= 1L;

	/**
	 * Create an interactive flow. The bandwidths and durations here are
	 * specified on a per-user basis. For routing and congestion purposes, this
	 * flow is treated as if it had a duration equal to the per-user duration
	 * times the number of users. This allows congestion algorithms to mimic the
	 * actual per-individual-user flow behavior of routers.
	 * 
	 * @param source
	 * @param destination
	 * @param user
	 * @param bandwidth
	 *            The bandwidth this flow uses *per-user*
	 * @param duration
	 *            How long this flow will be active <i>per user</i>. (The actual
	 *            duration will be scaled by user population)
	 */
	public UserInteractiveFlow(Network source, Network destination, Consumer user, double bandwidth, double duration) {
		super(source, destination, user, bandwidth, duration * user.getPopulation());
	}

	@Override
	public void congest(double bandwidth) {
		// duration stays constant, reducing bandwidth automatically reduces
		// usage since the latter is calculated rather than stored.
		// (see simternet.network.NetFlow)
		if (this.bandwidth > bandwidth) {
			this.bandwidth = bandwidth;
			congested = true;
		}
	}

	/*
	 * This flow will congest immediately; if bandwidth is reduced, total usage
	 * will be reduced.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.network.NetFlow#getCongestionBandwidth()
	 */
	@Override
	public Double getCongestionBandwidth() {
		return bandwidth;
	}

	@Override
	public double getCongestionDuration() {
		return duration;
	}

}
