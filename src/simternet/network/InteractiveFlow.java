package simternet.network;

import simternet.consumer.AbstractConsumerClass;

public class InteractiveFlow extends NetFlow {

	/**
	 * The speed this flow would <i>like</i> to have, in an uncongested network.
	 * This will be set for <i>interactive</i> flows, e.g., streaming.
	 * Non-interactive flows will transmit as quickly as possible.
	 */
	protected Double bandwidthRequested;

	/**
	 * Create an interactive flow
	 * 
	 * @param source
	 * @param destination
	 * @param user
	 * @param durationActual
	 *            Interactive flows last for a fixed duration.
	 * @param bandwidthRequested
	 *            How much bandwidth this flow needs
	 * @param bandwidthActual
	 *            Optional, used for flow control. Separate from
	 *            bandwidthRequested only so that the user can compare actual
	 *            and requested once they receive the flow.
	 */
	public InteractiveFlow(AbstractNetwork source, AbstractNetwork destination,
			AbstractConsumerClass user, Double durationActual,
			Double bandwidthRequested, Double bandwidthActual) {

		super(source, destination, user);
		this.bandwidthRequested = bandwidthRequested;

		// pre congest if requested
		if (bandwidthActual != null)
			this.bandwidth = bandwidthActual;
		else
			this.bandwidth = bandwidthRequested;

		this.duration = durationActual;

	}

	@Override
	public void congest(Double bandwidth) {
		// duration stays constant, reducing bandwidth automatically reduces
		// usage since the latter is not stored in a separate variable.
		if (this.bandwidth > bandwidth) {
			this.bandwidth = bandwidth;
			this.congested = true;
		}
	}

	@Override
	public String describeCongestion() {
		return this.bandwidth + "/" + this.bandwidthRequested;
	}

	@Override
	public Double getCongestionBandwidth() {
		return this.bandwidth;
	}

	@Override
	public Double getCongestionDuration() {
		return this.duration * this.user.getPopultation();
	}

}