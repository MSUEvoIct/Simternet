package simternet;

import sim.engine.SimState;

/**
 * @author kkoning
 *
 * When agents update data upon which other agents will take action, 
 * syncronously updating these values at decision time potentially
 * creates race conditions.  To avoid this problem, Simternet agents
 * (and their data objects) implement this interface to separate the
 * decision process from the resulting changes to data structures.
 *
 */
public interface AsyncUpdater {
	public void updateData(SimState state);
}
