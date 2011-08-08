package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasPrice;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * A simple price; exactly what it represents depends on the problem.
 * 
 * @author kkoning
 * 
 */
public class Price extends GPNode {

	@Override
	public String toString() {
		return "Price";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		HasPrice hp = (HasPrice) problem;

		DoubleGP dd = (DoubleGP) input;
		dd.value = hp.getPrice();

	}

}
