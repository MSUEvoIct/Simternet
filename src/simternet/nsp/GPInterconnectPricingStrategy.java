package simternet.nsp;

import java.io.Serializable;

import simternet.application.ApplicationProvider;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.PriceASPTransitProblem;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPInterconnectPricingStrategy implements NSPInterconnectPricingStrategy, Serializable {

	private static final Double		MAX_PRICE	= 1.0;	// $1/byte is very high
	private static final Double		MIN_PRICE	= 0.0;
	protected final GPIndividual	ind;
	protected final NetworkProvider	nsp;
	protected final GPTree			transitPricingTree;

	public GPInterconnectPricingStrategy(NetworkProvider nsp, GPIndividual ind, GPTree transitPricingTree) {
		this.ind = ind;
		this.transitPricingTree = transitPricingTree;
		this.nsp = nsp;
	}

	@Override
	public Double getASPTransitPrice(ApplicationProvider other) {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;

		PriceASPTransitProblem patp = new PriceASPTransitProblem(this.nsp, other);

		this.transitPricingTree.child.eval(null, 0, d, null, this.ind, patp);

		if (d.value < GPInterconnectPricingStrategy.MIN_PRICE)
			d.value = GPInterconnectPricingStrategy.MIN_PRICE;

		if (d.value > GPInterconnectPricingStrategy.MAX_PRICE)
			d.value = GPInterconnectPricingStrategy.MAX_PRICE;

		return d.value;
	}
}
