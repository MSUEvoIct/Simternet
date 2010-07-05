package simternet.consumer;

import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;

public class ApplicationUsage implements AsyncUpdate {
	protected Temporal<Double> congestionReceived;
	protected Temporal<Double> usageAmount;

	public ApplicationUsage() {
		this.congestionReceived = new Temporal<Double>(0.0, 0.0);
		this.usageAmount = new Temporal<Double>(0.0, 0.0);
	}

	@Override
	public void update() {
		this.usageAmount.update();
		this.congestionReceived.update();
	}
}
