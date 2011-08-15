package simternet.ecj.problems;

import sim.util.Int2D;
import simternet.Financials;
import simternet.Simternet;
import simternet.network.BackboneLink;
import simternet.network.EdgeNetwork;
import simternet.nsp.NetworkProvider;
import ec.Problem;

public class EdgeBackboneUpgradeProblem extends Problem implements HasNetworkProvider, HasFinancials, HasLocation,
		HasSimternet, HasBackboneLink, HasEdgeNetwork {
	private static final long	serialVersionUID	= 1L;
	private NetworkProvider		nsp;
	private EdgeNetwork			edge;

	public EdgeBackboneUpgradeProblem(NetworkProvider nsp, EdgeNetwork edge) {
		this.nsp = nsp;
		this.edge = edge;
	}

	@Override
	public EdgeNetwork getEdgeNetwork() {
		return edge;
	}

	@Override
	public BackboneLink getBackboneLink() {
		return edge.getUpstreamIngress();
	}

	@Override
	public Simternet getSimternet() {
		return nsp.s;
	}

	@Override
	public Int2D location() {
		return edge.getLocation();
	}

	@Override
	public Financials getFinancials() {
		return nsp.getFinancials();
	}

	@Override
	public NetworkProvider getNetworkProvider() {
		return nsp;
	}

}
