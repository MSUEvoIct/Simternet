package simternet.ecj.problems;

import simternet.Financials;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.network.BackboneLink;
import simternet.nsp.NetworkProvider;
import ec.Problem;

public class ASPPurchaseTransitProblem extends Problem implements HasSimternet, HasFinancials, HasNetworkProvider,
		HasApplicationProvider, HasPrice, HasBackboneLink {

	protected final ApplicationProvider	customer;
	protected final BackboneLink		existing;
	protected final NetworkProvider		nsp;
	protected final double				price;
	private static final long			serialVersionUID	= 1L;

	public ASPPurchaseTransitProblem(ApplicationProvider asp, NetworkProvider nsp, BackboneLink existing) {
		customer = asp;
		this.nsp = nsp;
		this.existing = existing;
		price = nsp.getASPTransitPrice(asp);
	}

	@Override
	public ApplicationProvider getApplicationProvider() {
		return customer;
	}

	@Override
	public Financials getFinancials() {
		return customer.getFinancials();
	}

	@Override
	public NetworkProvider getNetworkProvider() {
		return nsp;
	}

	@Override
	public Simternet getSimternet() {
		return nsp.s;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public BackboneLink getBackboneLink() {
		return existing;
	}

}
