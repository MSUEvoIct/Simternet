package simternet.ecj.problems;

import simternet.network.EdgeNetwork;
import simternet.nsp.NetworkProvider;
import ec.Problem;

/**
 * This class contains the data structures that must be made available when
 * making the decision regarding how to price an edge network. However, the
 * individual pieces of data will not actually be used by the GP-generated
 * algorithm unless the corresponding GPNode terminals are listed as potential
 * functions in this tree.
 * 
 * @author kkoning
 * 
 */
public class PriceEdgeNetworkProblem extends Problem implements HasEdgeNetwork {

	private static final long		serialVersionUID	= 1L;
	private EdgeNetwork		aen;
	private NetworkProvider	nsp;

	public PriceEdgeNetworkProblem(NetworkProvider nsp, EdgeNetwork aen) {
		this.nsp = nsp;
		this.aen = aen;
	}

	@Override
	public EdgeNetwork getEdgeNetwork() {
		return this.aen;
	}

	public NetworkProvider getNsp() {
		return this.nsp;
	}

}