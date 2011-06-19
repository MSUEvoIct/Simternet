package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasEdgeNetwork;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class PreviousEdgePrice extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		double previousPrice = ((HasEdgeNetwork) problem).getEdgeNetwork().getPrice();

		DoubleGP dd = (DoubleGP) input;
		dd.value = previousPrice;

	}

	@Override
	public String toString() {
		return "PreviousEdgePrice";
	}

}
