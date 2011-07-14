package simternet.network;

import sim.engine.SimState;
import simternet.nsp.NetworkProvider;

/**
 * BackboneNetworks are those operated by Network Service Providers. They
 * connect to their own edge networks, other network providers, and some
 * application service providers.
 * 
 * @author kkoning
 * 
 */
public class Backbone extends Network {

	protected final NetworkProvider	owner;
	private static final long		serialVersionUID	= 1L;

	public Backbone(final NetworkProvider nsp) {
		this.owner = nsp;
	}

	public NetworkProvider getOwner() {
		return this.owner;
	}

	@Override
	public void step(SimState state) {
		super.step(state);
	}

	@Override
	public String toString() {
		return "Backbone of " + this.owner.toString();
	}

}
