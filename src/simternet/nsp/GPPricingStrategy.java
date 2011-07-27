package simternet.nsp;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.PriceEdgeNetworkProblem;
import simternet.network.EdgeNetwork;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPPricingStrategy extends PricingStrategy {

	public static final Double	MAX_PRICE			= 1.0E9;
	/**
	 * Rediculous minimal price just prevents infinite negative prices etc...
	 */
	public static final Double	MIN_PRICE			= -1.0E9;

	private static final long	serialVersionUID	= 1L;
	GPIndividual				individual			= null;
	GPTree						pricingTree			= null;

	public GPPricingStrategy(NetworkProvider nsp, GPIndividual individual, GPTree tree) {
		super(nsp);
		this.pricingTree = tree;
		this.individual = individual;
	}

	@Override
	protected Double calculateEdgePrice(EdgeNetwork edge) {

		DoubleGP d = new DoubleGP();
		d.value = 0;

		PriceEdgeNetworkProblem penp = new PriceEdgeNetworkProblem(this.nsp, edge);
		this.pricingTree.child.eval(null, 0, d, null, this.individual, penp);

		if (d.value < GPPricingStrategy.MIN_PRICE)
			return GPPricingStrategy.MIN_PRICE;
		else if ((d.value > GPPricingStrategy.MAX_PRICE) || (d.value == Double.NaN))
			return GPPricingStrategy.MAX_PRICE;
		else
			return d.value;
	}

}
