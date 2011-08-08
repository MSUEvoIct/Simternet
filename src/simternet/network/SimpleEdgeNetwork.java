package simternet.network;

import sim.util.Int2D;
import simternet.nsp.NetworkProvider;

public class SimpleEdgeNetwork extends EdgeNetwork {

	private static final long	serialVersionUID	= 1L;

	public SimpleEdgeNetwork(NetworkProvider owner, Int2D location) {
		super(owner, location);
	}

	@Override
	public Double getOperationCost() {
		double variableCost = owner.s.config.networkSimpleOpCostPerUser * getNumSubscribers();
		double fixedCost = owner.s.config.networkSimpleOpCostFixed;
		double totalOperationCost = variableCost + fixedCost;
		return totalOperationCost;
	}

}
