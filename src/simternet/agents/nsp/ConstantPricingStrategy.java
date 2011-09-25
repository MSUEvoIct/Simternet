package simternet.agents.nsp;

import simternet.network.EdgeNetwork;

public class ConstantPricingStrategy extends PricingStrategy {

	private static final long	serialVersionUID	= 1L;

	protected Double			price;

	public ConstantPricingStrategy(NetworkProvider nsp, Double price) {
		super(nsp);
		this.price = price;
	}

	@Override
	protected Double calculateEdgePrice(EdgeNetwork edge) {
		return this.price;
	}

}
