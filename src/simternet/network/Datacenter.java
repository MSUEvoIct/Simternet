package simternet.network;

import sim.engine.SimState;
import simternet.application.ApplicationServiceProvider;
import simternet.temporal.TemporalHashMap;

public class Datacenter extends AbstractNetwork {

	private static final long serialVersionUID = 1L;
	protected TemporalHashMap<ApplicationServiceProvider, Link> customers = new TemporalHashMap<ApplicationServiceProvider, Link>();

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
