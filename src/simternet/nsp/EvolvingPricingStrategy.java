package simternet.nsp;

import java.io.Serializable;

import simternet.network.EdgeNetwork;

public class EvolvingPricingStrategy extends PricingStrategy implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected NetworkProvider	nsp;
	protected Double			price;

	public EvolvingPricingStrategy(NetworkProvider nsp, Double price) {
		super(nsp);
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
	// @Override
	// public Double getPrice(Class<? extends AbstractNetwork> cl,
	// AbstractConsumerClass cc, Int2D location) {
	// return this.price;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see simternet.nsp.PricingStrategy#setPrices(simternet.Simternet,
	 * sim.field.grid.SparseGrid2D)
	 * 
	 * Grabs the global price set by the LCS
	 */
	// @Override
	// public void setPrices() {
	// this.setSinglePrice(this.nsp.getPrice(null, null, null));
	// }

	@Override
	protected Double calculateEdgePrice(EdgeNetwork edge) {
		return this.price;
	}

	/*
	 * Sets the price for all tiles on the grid
	 */
	public void setSinglePrice(Double price) {
		this.price = price;
	}

}
