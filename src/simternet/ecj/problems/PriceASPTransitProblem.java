package simternet.ecj.problems;

import simternet.Financials;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.nsp.NetworkProvider;
import simternet.nsp.PotentialNetwork;
import ec.Problem;

public class PriceASPTransitProblem extends Problem implements HasSimternet, HasFinancials, HasNetworkProvider,
		HasApplicationProvider {

	private static final long			serialVersionUID	= 1L;
	protected final ApplicationProvider	customer;

	protected final NetworkProvider		isp;

	public PriceASPTransitProblem(NetworkProvider isp, ApplicationProvider customer) {
		this.isp = isp;
		this.customer = customer;
	}

	@Override
	public ApplicationProvider getApplicationProvider() {
		return this.customer;
	}

	@Override
	public Financials getFinancials() {
		return this.isp.financials;
	}

	@Override
	public NetworkProvider getNetworkProvider() {
		return this.isp;
	}

	@Override
	public PotentialNetwork getPotentialNetwork() {
		// TODO: Why is this here?
		return null;
	}

	@Override
	public Simternet getSimternet() {
		return this.isp.simternet;
	}

}
