package simternet.nsp;

import java.io.Serializable;

import simternet.network.EdgeNetwork;

public abstract class PricingStrategy implements Serializable {

	private static final long	serialVersionUID	= 1L;

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
			// If the price is more than the consumer will ever pay, set a price
			// of that number + 1 (prevent 1E8 prices, throwing off averages
			// etc...)
			if (price > nsp.s.config.consumerMaxPriceNSP) {
				price = nsp.s.config.consumerMaxPriceNSP + 1;
			}

			edge.setPrice(price);
		}
	}

}
