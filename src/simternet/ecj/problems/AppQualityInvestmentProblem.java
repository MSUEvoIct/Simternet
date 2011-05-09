package simternet.ecj.problems;

import simternet.Financials;
import simternet.application.ApplicationProvider;
import ec.Problem;

public class AppQualityInvestmentProblem extends Problem implements HasApplicationProvider, HasFinancials {

	private static final long			serialVersionUID	= 1L;
	private final ApplicationProvider	asp;
	private final Financials			f;

	public AppQualityInvestmentProblem(ApplicationProvider asp, Financials f) {
		this.asp = asp;
		this.f = f;
	}

	@Override
	public ApplicationProvider getApplicationProvider() {
		return this.asp;
	}

	@Override
	public Financials getFinancials() {
		return this.f;
	}

}
