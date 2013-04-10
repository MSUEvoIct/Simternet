package simternet.consumer;

import simternet.Simternet;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public interface PopulationInitializer {
	/**
	 * Initializes the population array of the consumer object.
	 * 
	 * @param s
	 * @param c
	 */
	public void populate(Simternet s, Consumer c);
	
	public void setup(ParameterDatabase pd, Parameter base);
	
}
