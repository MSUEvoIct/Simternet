package simternet.network;

import sim.engine.SimState;
import simternet.nsp.AbstractNetworkProvider;

/**
 * BackboneNetworks are those operated by Network Service Providers. They
 * connect to their own edge networks, other network providers, and some
 * application service providers.
 * 
 * @author kkoning
 * 
 */
public class BackboneNetwork extends AbstractNetwork {

	private static final long				serialVersionUID	= 1L;
	protected final AbstractNetworkProvider	owner;

	public BackboneNetwork(final AbstractNetworkProvider nsp) {
		this.owner = nsp;
	}

	public AbstractNetworkProvider getOwner() {
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
