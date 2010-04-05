package simternet.temporal;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Simternet;
import simternet.nsp.AbstractNetworkProvider;

/**
 * @author kkoning
 * 
 *         TODO: Extend this class to also handle updating consumers, as soon as
 *         they implement AsyncUpdate
 * 
 */
public class Arbiter implements Steppable {
	private static final long serialVersionUID = 1L;

	@Override
	public void step(SimState state) {
		for (AbstractNetworkProvider nsp : ((Simternet) state)
				.getNetworkServiceProviders())
			nsp.update();
	}

}
