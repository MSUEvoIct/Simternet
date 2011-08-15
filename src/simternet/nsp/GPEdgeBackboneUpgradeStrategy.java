package simternet.nsp;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.EdgeBackboneUpgradeProblem;
import simternet.network.EdgeNetwork;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPEdgeBackboneUpgradeStrategy extends EdgeBackboneUpgradeStrategy {
	private static final long	serialVersionUID	= 1L;

	private GPIndividual		individual;
	private GPTree				decisionTree;

	public GPEdgeBackboneUpgradeStrategy(NetworkProvider nsp, GPIndividual ind, GPTree tree) {
		super(nsp);
		individual = ind;
		decisionTree = tree;
	}

	@Override
	public double determineCapacityToAdd(EdgeNetwork en) {
		DoubleGP capacityToAdd = new DoubleGP();
		capacityToAdd.value = 0D;

		EdgeBackboneUpgradeProblem ebup = new EdgeBackboneUpgradeProblem(nsp, en);
		decisionTree.child.eval(null, 0, capacityToAdd, null, individual, ebup);

		return capacityToAdd.value;
	}

}
