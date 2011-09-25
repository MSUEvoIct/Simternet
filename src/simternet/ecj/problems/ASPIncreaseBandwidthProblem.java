package simternet.ecj.problems;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.finance.Financials;
import simternet.engine.Simternet;
import ec.Problem;

public class ASPIncreaseBandwidthProblem extends Problem implements HasApplicationProvider, HasFinancials, HasSimternet {

	protected final ApplicationProvider	asp;

	public ASPIncreaseBandwidthProblem(ApplicationProvider asp) {
		this.asp = asp;
	}

	@Override
	public ApplicationProvider getApplicationProvider() {
		return this.asp;
	}

	@Override
	public Financials getFinancials() {
		return this.asp.getFinancials();
	}

	@Override
	public Simternet getSimternet() {
		return this.asp.s;
	}

}
