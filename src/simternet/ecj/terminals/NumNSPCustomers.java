package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasNetworkProvider;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class NumNSPCustomers extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		DoubleGP dd = (DoubleGP) input;

		HasNetworkProvider hnp = (HasNetworkProvider) problem;
		dd.value = hnp.getNetworkProvider().getCustomers();

	}

	@Override
	public String toString() {
		return "NumNSPCustomers";
	}

}
