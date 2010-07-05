package simternet.consumer;

import simternet.application.ApplicationServiceProvider;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;
import simternet.temporal.TemporalHashMap;

/**
 * Describes a Consumer Class's usage of the a network they are subscribed to.
 * 
 * @author kkoning
 * 
 */
public class NetworkUsageDetails implements AsyncUpdate {

	protected TemporalHashMap<ApplicationServiceProvider, ApplicationUsage> applicationUsage;
	protected Temporal<Double> periodBandwidth;
	/**
	 * The number of individual consumers with connections to the network.
	 */
	protected Temporal<Double> subscribers;

	public NetworkUsageDetails() {
		this.applicationUsage = new TemporalHashMap<ApplicationServiceProvider, ApplicationUsage>();
		this.periodBandwidth = new Temporal<Double>(0.0, 0.0);
		this.subscribers = new Temporal<Double>(0.0);
	}

	public TemporalHashMap<ApplicationServiceProvider, ApplicationUsage> getApplicationUsage() {
		return this.applicationUsage;
	}

	public Temporal<Double> getPeriodBandwidth() {
		return this.periodBandwidth;
	}

	public Double getSubscribers() {
		return this.subscribers.get();
	}

	public void setApplicationUsage(
			TemporalHashMap<ApplicationServiceProvider, ApplicationUsage> applicationUsage) {
		this.applicationUsage = applicationUsage;
	}

	public void setPeriodBandwidth(Temporal<Double> periodBandwidth) {
		this.periodBandwidth = periodBandwidth;
	}

	public void setSubscribers(Double subscribers) {
		this.subscribers.set(subscribers);
	}

	@Override
	public void update() {
		this.applicationUsage.update();
		this.periodBandwidth.update();
		this.subscribers.update();
	}

}
