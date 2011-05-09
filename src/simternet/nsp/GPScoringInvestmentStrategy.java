package simternet.nsp;

import java.util.List;

import org.apache.log4j.Logger;

import simternet.TraceConfig;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.ScoreEdgeBuildProblem;
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
		this.scoringTree = tree;
	}

	@Override
	protected void scoreSimpleNetwork(PotentialNetwork pn) {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;

		ScoreEdgeBuildProblem sebp = new ScoreEdgeBuildProblem(this.nsp, pn);
		this.scoringTree.child.eval(null, 0, d, null, this.individual, sebp);
		pn.score = d.value;

		if (TraceConfig.potentialNetworkScoring && Logger.getRootLogger().isTraceEnabled())
			Logger.getRootLogger().trace("Scoring PN:" + pn + ", Score: " + pn.score);

	}

}
