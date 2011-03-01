package simternet.nsp;

import java.io.Serializable;

import simternet.network.EdgeNetwork;

public abstract class PricingStrategy implements Serializable {

	private static final long			serialVersionUID	= 1L;

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
	 * By default,
	 */
	public void priceEdges() {
		for (EdgeNetwork edge : this.nsp.getEdgeNetworks()) {
			Double price = this.calculateEdgePrice(edge);
			edge.setPrice(price);
		}
	}

}
