package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class Seven extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		DoubleGP d = (DoubleGP) input;
		d.value = 7;
	}

	@Override
	public String toString() {
		return "7";
	}

}
