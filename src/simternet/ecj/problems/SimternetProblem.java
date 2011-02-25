package simternet.ecj.problems;

import simternet.ecj.SimternetGPIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;

public class SimternetProblem extends GPProblem implements SimpleProblemForm {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		SimpleFitness f = ((SimpleFitness) ind.fitness);
		SimternetGPIndividual sgpi = (SimternetGPIndividual) ind;
		f.setFitness(state, sgpi.getAgent().getFitness().floatValue(), false);
		ind.evaluated = true;

	}

}
