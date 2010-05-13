package simternet;

import java.io.Serializable;
import java.util.Set;

import sim.engine.SimState;
import simternet.consumer.CournotConsumer;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.CournotNetworkServiceProvider;
import simternet.temporal.Arbiter;

public class CournotSimternet extends Simternet implements Serializable {

	public final static Double ALPHA = 100.0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SimState.doLoop(CournotSimternet.class, args);
		System.exit(0);
	}

	public CournotSimternet(long seed) {
		super(seed);
	}

	public Double getCombinedCompetitorsMarketShare(
			CournotNetworkServiceProvider np) {
		double sum = 0.0;
		Set<AbstractNetworkProvider> nsps = this.getNetworkServiceProviders();
		for (AbstractNetworkProvider nsp : nsps)
			if (((CournotNetworkServiceProvider) nsp)
					.getPreviousTotalSubscribers().isNaN() == false)
				sum += ((CournotNetworkServiceProvider) nsp)
						.getPreviousTotalSubscribers();

		if (sum == 0)
			return new Double(0.0);
		if (sum - np.getPreviousTotalSubscribers() == 0)
			return new Double(0.0);
		Double marketShare = new Double(
				(sum - np.getPreviousTotalSubscribers()) / this.getPopulation());
		return marketShare;
	}

	@Override
	protected void initConsumerClasses() {
		this.addConsumerClass(new CournotConsumer(this));
	}

	private void initNetworkServiceProviders() {
		this.addNetworkServiceProvider(new CournotNetworkServiceProvider(this));
		this.addNetworkServiceProvider(new CournotNetworkServiceProvider(this));
		this.addNetworkServiceProvider(new CournotNetworkServiceProvider(this));
	}

	@Override
	public void start() {
		// reset schedule
		this.schedule.reset();
		this.initNetworkServiceProviders();
		this.initConsumerClasses();
		this.schedule.scheduleRepeating(new Arbiter(), 99999999, 1);
	}
}
