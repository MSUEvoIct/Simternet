package simternet.network;

import java.io.Serializable;

import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.nsp.AbstractNetworkProvider;
import simternet.temporal.AsyncUpdate;

public abstract class AbstractNetwork implements AsyncUpdate, Steppable,
		Serializable {

	private static final long serialVersionUID = 1L;
	protected Int2D location;
	protected AbstractNetworkProvider owner;

	public abstract Double getBuildCost();

	public Int2D getLocation() {
		return this.location;
	}

	public void init(final AbstractNetworkProvider nsp, final Int2D location) {
		this.owner = nsp;
		this.location = location;
	}

}
