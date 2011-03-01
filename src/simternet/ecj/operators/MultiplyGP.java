package simternet.ecj.operators;

import simternet.ecj.DoubleGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class MultiplyGP extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		DoubleGP result = (DoubleGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);
		double first = result.value;

		this.children[1].eval(state, thread, result, stack, individual, problem);

		result.value *= first;

	}

	@Override
	public String toString() {
		return "*";
	}

}
