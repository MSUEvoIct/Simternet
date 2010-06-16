package simternet.nsp;

import java.io.Serializable;

import sim.util.Int2D;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;

public class ConstantPricingStrategy implements PricingStrategy, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected AbstractNetworkProvider nsp;
	protected Double price;

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
			AbstractConsumerClass cc, Int2D location) {
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
