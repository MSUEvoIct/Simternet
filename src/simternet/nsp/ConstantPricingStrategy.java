package simternet.nsp;

import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;

public class ConstantPricingStrategy implements PricingStrategy {

	protected Double price;
	protected AbstractNetworkProvider nsp;

	public ConstantPricingStrategy(AbstractNetworkProvider nsp, Double price) {
		this.nsp = nsp;
		this.price = price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simternet.nsp.PricingStrategy#getPrice(java.lang.Class,
	 * simternet.consumer.AbstractConsumerClass, int, int)
	 * 
	 * Always returns the same price, regardless of network type, consumer
	 * class, or specific network (or location)
	 */
	@Override
	public Double getPrice(Class<? extends AbstractNetwork> cl,
			AbstractConsumerClass cc, int x, int y) {
		return this.price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simternet.nsp.PricingStrategy#setPrices(simternet.Simternet,
	 * sim.field.grid.SparseGrid2D)
	 * 
	 * Does nothing, since we always return a constant price anyway.
	 */
	@Override
	public void setPrices() {

	}

}
