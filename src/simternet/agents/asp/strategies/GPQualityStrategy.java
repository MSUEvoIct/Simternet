package simternet.agents.asp.strategies;

import simternet.agents.asp.GPApplicationProvider;
import simternet.agents.asp.Quality;
import simternet.agents.asp.QualityStrategy;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.AppQualityInvestmentProblem;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPQualityStrategy extends QualityStrategy {

	private static final long				serialVersionUID	= 1L;
	protected final GPApplicationProvider	asp;
	protected final GPIndividual			ind;
	protected final GPTree					tree;

	public GPQualityStrategy(GPApplicationProvider asp, GPIndividual ind, GPTree tree) {
		super(asp);
		this.asp = asp;
		this.ind = ind;
		this.tree = tree;
	}

	@Override
	public void investInQuality() {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;

		AppQualityInvestmentProblem aqip = new AppQualityInvestmentProblem(asp, asp.getFinancials());
		tree.child.eval(null, 0, d, null, ind, aqip);

		double amountToIncrease = d.value;
		if (amountToIncrease < 0) {
			amountToIncrease = 0.0;
		}

		Quality.increaseQuality(asp, amountToIncrease);
	}

}
