package simternet.network;

import simternet.agents.consumer.Consumer;

public class UserBackgroundFlow extends NetFlow {

	/**
	 * The maximum length of time a non-interactive flow may take to transit the
	 * network. If the total requested amount cannot be transmitted in this time
	 * due to congestion, this flow will reduce its overall consumption.
	 */
	protected double	maxDuration;

	/**
	 * Create a non-interactive flow. Non-interactive flows are those that
	 * continue to transmit a given amount of data even if the transfer is slow
	 * due to network congestion. There is, however, an upper limit on the
	 * amount of time this may take, after which usage is reduced. (i.e., users
	 * will adjust how much data they will send in this manner.
	 * 
	 * These flows are important to model separately because (1) they are less
	 * sensitive to congestion (users care less if they're slow, as long as
	 * they're not *too* slow) and (2) because they continue transmitting even
	 * at a lower speed they'll ultimately use more resources than interactive
	 * flows. (Think more flows overlapping...)
	 * 
	 * @param source
	 * @param destination
	 * @param user
	 * @param totalTransfer
	 *            The total amount of data to transfer <i>per user</i>, in bytes
	 * @param maxDuration
	 *            The maximum time to wait for this data transfer.
	 */
	public UserBackgroundFlow(Network source, EdgeNetwork destination, Consumer user, double totalTransfer,
			Double maxDuration) {
		super(source, destination, user, destination.getMaxBandwidth(), totalTransfer / destination.getMaxBandwidth()
				* user.getPopulation());

		this.maxDuration = maxDuration * user.getPopulation();

	}

	@Override
	public void congest(double bandwidthLimit) {
		if (bandwidth > bandwidthLimit) {
			congested = true;

			// Usage remains constant until maxDuration is exceeded
			double usage = bandwidth * duration;
			bandwidth = bandwidthLimit;

			double newDuration = usage / bandwidthLimit;
			if (newDuration <= maxDuration) {
				duration = newDuration;
			} else {
				duration = maxDuration;
			}
		}
	}

	@Override
	public Double getCongestionBandwidth() {
		return getActualTransfer() / maxDuration;
	}

	@Override
	public double getCongestionDuration() {
		return maxDuration;
	}
}
