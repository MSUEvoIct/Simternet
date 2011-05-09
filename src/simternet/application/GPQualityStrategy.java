package simternet.application;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.AppQualityInvestmentProblem;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPQualityStrategy extends QualityStrategy {

	protected final GPIndividual	ind;
	protected final GPTree			tree;

	public GPQualityStrategy(ApplicationProvider asp, GPIndividual ind, GPTree tree) {
		super(asp);
		this.ind = ind;
		this.tree = tree;
	}

	@Override
	public void investInQuality() {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;

		AppQualityInvestmentProblem aqip = new AppQualityInvestmentProblem(this.asp, this.asp.financials);
		this.tree.child.eval(null, 0, d, null, this.ind, aqip);

		Quality.increaseQuality(this.asp, d.value);
	}

}
