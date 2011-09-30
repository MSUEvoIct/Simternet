package simternet.engine;

import java.io.Serializable;

import sim.engine.Steppable;
import simternet.engine.asyncdata.AsyncUpdate;

public interface Agent extends Steppable, AsyncUpdate, Serializable {
	public abstract String getName();

}
