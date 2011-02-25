package simternet.ecj;

import sim.engine.Steppable;
import ec.Individual;

public interface EvolvableAgent extends Steppable {
	public Double getFitness();

	public Individual getIndividual();

	public void setIndividual(Individual i);
}
