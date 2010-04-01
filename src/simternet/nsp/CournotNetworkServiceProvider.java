package simternet.nsp;

import javax.activation.UnsupportedDataTypeException;

import sim.engine.SimState;
import simternet.*;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.SimpleNetwork;

public class CournotNetworkServiceProvider extends AbstractNetworkProvider{
	private static final long serialVersionUID = -9165331810723302112L;
	
	private Boolean built = false;

	private Temporal<Double> price = new Temporal<Double>(0.0, 0.0);
	
	private Temporal<Double> totalSubscribers = new Temporal<Double>(0.0, 0.0);
	
	public CournotNetworkServiceProvider(Simternet s){
		super(s);
		setPrices();
		try {
			price.update();
		} catch (UnsupportedDataTypeException e) {
			//Shouldn't have caught this in the first place, but step() can't throw anything unless
			//I modify the source of Mason :\
			e.printStackTrace();
			System.exit(1);
		}
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
		return getPrice();
	}
	
	public Double getPrice() {
		return new Double(price.getOld());
	}
	
	@Override
	protected void setPrices() {
		Double p;
		p = new Double((CournotSimternet.ALPHA - (((CournotSimternet)simternet).getCombinedCompetitorsMarketShare(this)*100)) / 2);
		price.setNew(p);
	}

	public void setTotalSubscribers(Double totalSubscribers) {
		this.totalSubscribers.setNew(totalSubscribers);
	}

	public Double getTotalSubscribers() {
		return new Double(totalSubscribers.getNew());
	}

	public Double getPreviousTotalSubscribers() {
		return new Double(totalSubscribers.getOld());
	}
	
	@Override
	public void step(SimState state) {
		super.step(state);
	}

	@Override
	public void updateData(SimState state) throws UnsupportedDataTypeException {
		totalSubscribers.update();
		price.update();
	}
	
}
