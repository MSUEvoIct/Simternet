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

	private static final long				serialVersionUID	= 1L;
	protected final NetworkProvider	owner;

	public Backbone(final NetworkProvider nsp) {
		this.owner = nsp;
	}

	public NetworkProvider getOwner() {
		return this.owner;
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		super.step(state);
	}

	@Override
	public String toString() {
		return "Backbone of " + this.owner.getName();
	}

}