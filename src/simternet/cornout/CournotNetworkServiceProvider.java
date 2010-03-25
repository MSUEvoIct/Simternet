package simternet.cornout;

import sim.engine.SimState;
import simternet.main.*;

public class CournotNetworkServiceProvider extends AbstractNetworkProvider{
	private static final long serialVersionUID = -9165331810723302112L;

	private static final Double ZERO = 0.0;
	
	private Boolean built = false;

	private Double price = 0.0;
	
	private Double marketShare = 0.0;
	private Double previousMarketShare = Double.NaN;
	
	public CournotNetworkServiceProvider(Simternet s) {
		super(s);
	}
	
	/* (non-Javadoc)
	 * @see simternet.NetworkServiceProvider#makeNetworkInvestment()
	 * 
	 * Build a network at each and every location on the map.  Only
	 * do it once.
	 * 
	 */
	@Override
	protected void makeNetworkInvestment() {
		if (built == false)
			super.buildEverywhere(SimpleNetwork.class);
		built = true;
	}

	@SuppressWarnings("unchecked")
	public Double getPrice(Class cl, AbstractConsumer cc, int x, int y) {
		return price;
	}
	
	/* 
	 * Set the price in every square to the same static value.  Since we're
	 * building everywhere, don't bother only setting prices in locations at
	 * which we've built.
	 * 
	 */
	@Override
	protected void setPrices() {
		price = new Double( (CournotSimternet.ALPHA - getPrice()) / 2 );
	}

	public Double getPrice() {
		return price;
	}

	public void setMarketShare(Double marketShare) {
		this.marketShare = marketShare;
	}

	public Double getMarketShare() {
		return marketShare;
	}

	public Double getPreviousMarketShare() {
		return previousMarketShare;
	}
	
	public void advanceOneStep(){
		previousMarketShare = marketShare;
		marketShare = ZERO;
	}
	
	@Override
	public void step(SimState state) {
		super.step(state);
		System.out.println("    Market Share = " + getMarketShare() + " out of " + s.getPopulation());
		System.out.println("    Market Share = " + ((CournotSimternet)s).getCurrentMarketSharePercentage(this));
		System.out.println("    Price = " + getPrice());
		advanceOneStep();
	}
	
}
