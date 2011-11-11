package simternet.agents.nsp;

import java.util.List;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.ScoreEdgeBuildProblem;
import simternet.engine.TraceConfig;
import simternet.network.EdgeNetwork;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPScoringInvestmentStrategy extends ScoringInvestmentStrategy {

	private static final long	serialVersionUID	= 1L;
	GPIndividual				individual			= null;
	GPTree						scoringTree			= null;

	public GPScoringInvestmentStrategy(NetworkProvider nsp, List<Class<? extends EdgeNetwork>> networkTypes,
			GPIndividual individual, GPTree tree) {
		super(nsp, networkTypes, 0.0);
		this.individual = individual;
		scoringTree = tree;
	}

	@Override
	protected void scoreSimpleNetwork(PotentialNetwork pn) {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;

		ScoreEdgeBuildProblem sebp = new ScoreEdgeBuildProblem(nsp, pn);
		scoringTree.child.eval(null, 0, d, null, individual, sebp);
		pn.score = d.value;

		if (TraceConfig.potentialNetworkScoring) {
			TraceConfig.out.println("Scoring PN:" + pn + ", Score: " + pn.score);
		}

	}

}
