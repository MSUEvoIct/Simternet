package simternet.agents.nsp;

import java.io.Serializable;

import simternet.network.EdgeNetwork;

public abstract class PricingStrategy implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Rediculous minimum price just prevents infinite negative prices etc...
	 */
	public static final Double	MAX_PRICE			= 1000D;
	
	/**
	 * Price must now be non-negative, as it is raised to a fracitonal power.
	 */
	public static final Double	MIN_PRICE			= 0.1D;

	protected NetworkProvider	nsp;

	public PricingStrategy(NetworkProvider nsp) {
		this.nsp = nsp;
	}

	protected abstract Double calculateEdgePrice(EdgeNetwork edge);

	/**
	 * By default, just ask the edge what its price is.
	 * 
	 * Using this indirect accessor method allows for dynamically calculated
	 * prices. In other words, a child class could call calculateEdgePrice each
	 * time in turn, or always return a single price for the NSP.
	 * 
	 * @param edge
	 * @return The consumer price for using that edge network
	 */
	public Double getEdgePrice(EdgeNetwork edge) {
		return edge.getPrice();
	}

	/**
	 * 
	 */
	public void priceEdges() {
		for (EdgeNetwork edge : nsp.getEdgeNetworks()) {
			Double price = calculateEdgePrice(edge);

			// Adjust prices to sane limits; do not allow 1.0E9 prices to throw
			// off averages
			if (price < PricingStrategy.MIN_PRICE || price.isNaN() ) {
				price = PricingStrategy.MIN_PRICE;
			}
			if (price > PricingStrategy.MAX_PRICE) {
				price = PricingStrategy.MAX_PRICE;
			}

			edge.setPrice(price);
		}
	}

}
