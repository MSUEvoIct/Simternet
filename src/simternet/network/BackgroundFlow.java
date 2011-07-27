package simternet.network;

import simternet.consumer.Consumer;

public class BackgroundFlow extends NetFlow {

	/**
	 * The bandwidth at which the total amount of data transferred by this flow
	 * will be limited by congestion. In an interactive flow, this will be equal
	 * to maxBandwidth. In a non-interactive flow, it will be determined by the
	 * total amount to be transferred and maxTime.
	 */
	protected Double	bandwidthInteractive;

	/**
	 * The maximum length of time a non-interactive flow may take to transit the
	 * network. If the total requested amount cannot be transmitted in this time
	 * due to congestion, this flow will reduce the
	 */
	protected Double	maxDuration;

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
	 * @param usage
	 *            The total amount of data to transfer, in bytes
	 * @param maxDuration
	 *            The maximum time to wait for this data transfer.
	 */
	public BackgroundFlow(Network source, Network destination, Consumer user, Double usage, Double maxDuration) {

		super(source, destination, user);

		this.bandwidth = Double.MAX_VALUE;
		this.duration = usage / this.bandwidth;
		this.maxDuration = maxDuration;

	}

	@Override
	public void congest(Double bandwidth) {
		// Must temporarily store usage because changing duration or bandwidth
		// alters it.
		Double preCongestionUsage = this.bandwidth * this.duration;

		this.bandwidth = bandwidth;
		this.duration = preCongestionUsage / bandwidth;

		// Since usage is not stored as a variable separately from bandwidth *
		// duration, reducing the duration automatically reduces the usage.
		if (this.duration > this.maxDuration) {
			this.duration = this.maxDuration;
			this.congested = true;
		}

	}

	/*
	 * TODO: Implement
	 */
	@Override
	public String describeCongestion() {
		return "unimplemented";
	}

	@Override
	public Double getCongestionBandwidth() {
		return this.getUsage() / this.maxDuration;
	}

	@Override
	public Double getCongestionDuration() {
		return this.maxDuration * this.user.getPopulation();
	}

	@Override
	public Double getUsageBlocked() {
		// TODO: Implement
		return null;
	}

}
