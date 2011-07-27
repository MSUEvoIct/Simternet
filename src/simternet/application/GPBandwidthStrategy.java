package simternet.application;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.ASPIncreaseBandwidthProblem;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPBandwidthStrategy extends BandwidthStrategy {

	private static final long		serialVersionUID	= 1L;
	protected final GPIndividual	ind;
	protected final GPTree			tree;

	public GPBandwidthStrategy(ApplicationProvider asp, GPIndividual ind, GPTree tree) {
		super(asp);
		this.ind = ind;
		this.tree = tree;
	}

	@Override
	public Double increaseBandwidth() {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;
		ASPIncreaseBandwidthProblem aibp = new ASPIncreaseBandwidthProblem(this.asp);

		this.tree.child.eval(null, 0, d, null, this.ind, aibp);
		return d.value;
	}

}
