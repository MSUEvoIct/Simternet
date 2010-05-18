package simternet.nsp;

import sim.engine.SimState;
import simternet.CournotSimternet;
import simternet.Simternet;
import simternet.consumer.AbstractConsumerClass;
import simternet.temporal.Temporal;

public class CournotNetworkServiceProvider extends AbstractNetworkProvider {
	private static final long serialVersionUID = -9165331810723302112L;

	private Temporal<Double> price = new Temporal<Double>(0.0);
	private Temporal<Double> totalSubscribers = new Temporal<Double>(0.0);

	public CournotNetworkServiceProvider(Simternet s) {
		super(s);
		this.investmentStrategy = new BuildEverywhereStrategy(this,
				this.networks);
		this.setPrices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simternet.NetworkServiceProvider#makeNetworkInvestment()
	 * 
	 * Build a network at each and every location on the map. Only do it once.
	 */

	public Double getPreviousTotalSubscribers() {
		return new Double(this.totalSubscribers.get());
	}

	public Double getPrice() {
		return new Double(this.price.get());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Double getPrice(Class cl, AbstractConsumerClass cc, int x, int y) {
		return this.getPrice();
	}

	public Double getTotalSubscribers() {
		return new Double(this.totalSubscribers.get());
	}

	@Override
	protected void setPrices() {
		Double p;
		p = new Double(
				(CournotSimternet.ALPHA - (((CournotSimternet) this.simternet)
						.getCombinedCompetitorsMarketShare(this) * 100)) / 2);
		this.price.set(p);
	}

	public void setTotalSubscribers(Double totalSubscribers) {
		this.totalSubscribers.set(totalSubscribers);
	}

	@Override
	public void step(SimState state) {
		super.step(state);
	}

	@Override
	public void update() {
		this.totalSubscribers.update();
		this.price.update();
	}

}
