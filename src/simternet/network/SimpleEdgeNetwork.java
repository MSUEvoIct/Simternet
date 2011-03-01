package simternet.network;

import sim.util.Int2D;
import simternet.nsp.NetworkProvider;

public class SimpleEdgeNetwork extends EdgeNetwork {

	private static final long	serialVersionUID	= 1L;

	public static Double getBuildCost(NetworkProvider builder,
			Int2D location) {
		return 10000.0;
	}

	public SimpleEdgeNetwork(NetworkProvider owner, Int2D location) {
		super(owner, location);
	}

}
