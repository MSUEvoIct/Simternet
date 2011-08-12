package simternet.ecj.problems;

import simternet.Financials;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.network.BackboneLink;
import simternet.nsp.NetworkProvider;
import simternet.nsp.PotentialNetwork;
import ec.Problem;

/**
 * Financials are from the NSP, the BackboneLink is the one from the ASP to the
 * NSP.
 * 
 * @author kkoning
 * 
 */
public class PriceASPTransitProblem extends Problem implements HasSimternet, HasFinancials, HasNetworkProvider,
		HasApplicationProvider, HasBackboneLink {

	private static final long			serialVersionUID	= 1L;
	protected final ApplicationProvider	customer;

	protected final NetworkProvider		isp;

	public PriceASPTransitProblem(NetworkProvider isp, ApplicationProvider customer) {
		this.isp = isp;
		this.customer = customer;
	}

	@Override
	public ApplicationProvider getApplicationProvider() {
		return customer;
	}

	@Override
	public BackboneLink getBackboneLink() {
		return customer.getDatacenter().getEgressLink(isp.getBackboneNetwork());
	}

	@Override
	public Financials getFinancials() {
		return isp.financials;
	}

	@Override
	public NetworkProvider getNetworkProvider() {
		return isp;
	}

	@Override
	public PotentialNetwork getPotentialNetwork() {
		// TODO: Why is this here?
		return null;
	}

	@Override
	public Simternet getSimternet() {
		return isp.s;
	}

}
