package simternet.nsp;

import sim.engine.SimState;
import simternet.*;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.SimpleNetwork;

public class CournotNetworkServiceProvider extends AbstractNetworkProvider{
	private static final long serialVersionUID = -9165331810723302112L;
	
	private Boolean built = false;

	private Double price = 0.0;
	
	private Double totalSubscribers = 0.0;
	private Double previousTotalSubscribers = Double.NaN;
	
	private Boolean priceSet = false;
	
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
	public Double getPrice(Class cl, AbstractConsumerClass cc, int x, int y) {
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
		price = (CournotSimternet.ALPHA - (((CournotSimternet)simternet).getPreviousMarketSharePercentage(this)*100)) / 2;
		priceSet = true;
	}

	public Double getPrice() {
		return price;
	}

	public void setTotalSubscribers(Double marketShare) {
		this.totalSubscribers = marketShare;
	}

	public Double getTotalSubscribers() {
		return totalSubscribers;
	}

	public Double getPreviousTotalSubscribers() {
		return previousTotalSubscribers;
	}
	
	public void advanceOneStep(){
		previousTotalSubscribers = totalSubscribers;
		totalSubscribers = 0.0;
		priceSet = false;
	}
	
	@Override
	public void step(SimState state) {
		super.step(state);
	}

	public Boolean isPriceSet() {
		return priceSet;
	}
	
}
