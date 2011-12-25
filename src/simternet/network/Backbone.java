package simternet.network;

import sim.engine.SimState;
import simternet.agents.nsp.NetworkProvider;

/**
 * BackboneNetworks are those operated by Network Service Providers. They
 * connect to their own edge networks, other network providers, and some
 * application service providers.
 * 
 * @author kkoning
 * 
 */
public class Backbone extends Network {
	private static final long		serialVersionUID	= 1L;

	private final NetworkProvider	owner;

	public Backbone(final NetworkProvider nsp) {
		owner = nsp;
	}

	/**
	 * @return The Network Provider that owns this Backbone network
	 */
	public NetworkProvider getOwner() {
		return owner;
	}

	@Override
	public void step(SimState state) {
		super.step(state);
	}

	@Override
	public String toString() {
		return "Backbone of " + owner.getName();
	}

}
