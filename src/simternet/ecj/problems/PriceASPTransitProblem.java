package simternet.ecj.problems;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.finance.Financials;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;
import simternet.network.BackboneLink;
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
	public Simternet getSimternet() {
		return isp.s;
	}

}
