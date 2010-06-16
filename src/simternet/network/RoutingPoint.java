package simternet.network;

import sim.engine.SimState;
import simternet.temporal.TemporalHashMap;

public class RoutingPoint extends AbstractNetwork {

	protected TemporalHashMap<AbstractNetwork, Link> routingTable = new TemporalHashMap<AbstractNetwork, Link>();

	@Override
	public Double getBuildCost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
