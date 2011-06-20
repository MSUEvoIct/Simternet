package simternet.ecj.problems;

import simternet.Financials;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.network.BackboneLink;
import simternet.nsp.NetworkProvider;
import simternet.nsp.PotentialNetwork;
import ec.Problem;

public class ASPPurchaseTransitProblem extends Problem implements HasSimternet, HasFinancials, HasNetworkProvider,
		HasApplicationProvider {

	private static final long			serialVersionUID	= 1L;
	protected final ApplicationProvider	customer;
	protected final BackboneLink		existing;
	protected final NetworkProvider		nsp;

	public ASPPurchaseTransitProblem(ApplicationProvider asp, NetworkProvider nsp, BackboneLink existing) {
		this.customer = asp;
		this.nsp = nsp;
		this.existing = existing;
	}

	@Override
	public ApplicationProvider getApplicationProvider() {
		return this.customer;
	}

	@Override
	public Financials getFinancials() {
		return this.customer.getFinancials();
	}

	@Override
	public NetworkProvider getNetworkProvider() {
		return this.nsp;
	}

	@Override
	public PotentialNetwork getPotentialNetwork() {
		// TODO Auto-generated method stub
		// TODO This shouldn't be here
		return null;
	}

	@Override
	public Simternet getSimternet() {
		return this.nsp.simternet;
	}

}
