package simternet.network;

import sim.util.Int2D;
import simternet.nsp.AbstractNetworkProvider;

public class SimpleEdgeNetwork extends AbstractEdgeNetwork {

	private static final long serialVersionUID = 1L;

	public static Double getBuildCost(AbstractNetworkProvider builder,
			Int2D location) {
		return 10000.0;
	}

	public SimpleEdgeNetwork(AbstractNetworkProvider owner, Int2D location) {
		super(owner, location);
	}

}
