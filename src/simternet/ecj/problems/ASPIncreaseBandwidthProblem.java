package simternet.ecj.problems;

import simternet.Financials;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
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
