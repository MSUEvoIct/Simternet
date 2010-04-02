package simternet.consumer;

import java.util.Collections;
import java.util.Map;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.network.SimpleNetwork;
import simternet.nsp.AbstractNetworkProvider;

public class SimpleConsumer extends AbstractConsumerClass {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleConsumer(Simternet s) {
		super(s);
		networkTypesDemanded.add(SimpleNetwork.class);
	}
	
	@Override
	protected void makeConsumptionDecisionAt(Integer x, Integer y) {
		allocateSimpleNetworkAt(x,y);
	}
	
	/**
	 * @param x
	 * @param y
	 * 
	 * ToDo:  Very stupid method.  Qty demanded is set by the lowest market price, 
	 * and each and every NSP is given that number of customers.
	 */
	private void allocateSimpleNetworkAt(Integer x, Integer y) {
		Map<AbstractNetworkProvider,Double> prices = s.getPriceList(SimpleNetwork.class, this, x, y);
		if (prices == null) // no nsp has offered this service here
			return;
		if (prices.isEmpty()) // no nsp has offered this service here
			return;
		
		Double lowPrice = Collections.min(prices.values());
		Double qtyDemanded = demandSimpleNetwork(lowPrice,x,y);
		
		for(Map.Entry<AbstractNetworkProvider, Double> price: prices.entrySet()) {
			AbstractNetworkProvider nsp = price.getKey();
			// don't assign unless provider actually has the network in this location
			// todo: add null ptr checking on the other side of this set operation.
			this.setNumSubscriptions(SimpleNetwork.class, nsp, x, y, qtyDemanded);
		}
		
	}

	/* (non-Javadoc)
	 * @see simternet.AbstractConsumer#demand(java.lang.Class, java.lang.Double, java.lang.Integer, java.lang.Integer)
	 * 
	 * This class only demands SimpleNetwork; demand for all other networks is zero.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Double demand(Class network, Double price, Integer x, Integer y) {
		if (network.equals(SimpleNetwork.class)) {
			return demandSimpleNetwork(price,x,y);
		}
		return 0.0;
		
	}
	
	protected Double demandSimpleNetwork(Double price, Integer x, Integer y) {
		Double qty = (100 - price)/100 * this.getPopulation(x, y);
		if (qty > 0) 
			return qty;
		else
			return 0.0;
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		
	}

	@Override
	protected void initNetData() {
		
	}

}
