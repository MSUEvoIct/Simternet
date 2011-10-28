package simternet.agents.nsp;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.PriceEdgeNetworkProblem;
import simternet.network.EdgeNetwork;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPEdgePricingStrategy extends PricingStrategy {

	private static final long	serialVersionUID	= 1L;
	GPIndividual				individual			= null;
	GPTree						pricingTree			= null;

	public GPEdgePricingStrategy(NetworkProvider nsp, GPIndividual individual, GPTree tree) {
		super(nsp);
		pricingTree = tree;
		this.individual = individual;
	}

	@Override
	protected Double calculateEdgePrice(EdgeNetwork edge) {

		DoubleGP d = new DoubleGP();
		d.value = 0;

		PriceEdgeNetworkProblem penp = new PriceEdgeNetworkProblem(nsp, edge);
		pricingTree.child.eval(null, 0, d, null, individual, penp);
		return d.value;
	}

}
