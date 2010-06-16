package simternet.network;

import sim.engine.SimState;

public class SimpleEdgeNetwork extends AbstractEdgeNetwork {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see simternet.network.AbstractNetwork#getBuildCost()
	 */
	@Override
	public Double getBuildCost() {
		return 10000.0;
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
