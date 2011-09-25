package simternet.ecj.problems;

import simternet.agents.finance.Financials;
import simternet.agents.nsp.NetworkProvider;
import simternet.agents.nsp.PotentialNetwork;
import ec.Problem;

public class ScoreEdgeBuildProblem extends Problem implements HasNetworkProvider, HasPotentialNetwork, HasFinancials {

	private static final long	serialVersionUID	= 1L;
	private NetworkProvider		networkProvider;
	private PotentialNetwork	potentialNetwork;

	public ScoreEdgeBuildProblem(NetworkProvider nsp, PotentialNetwork pn) {
		this.potentialNetwork = pn;
		this.networkProvider = nsp;
	}

	@Override
	public Financials getFinancials() {
		return this.networkProvider.financials;
	}

	@Override
	public NetworkProvider getNetworkProvider() {
		return this.networkProvider;
	}

	@Override
	public PotentialNetwork getPotentialNetwork() {
		return this.potentialNetwork;
	}

}
