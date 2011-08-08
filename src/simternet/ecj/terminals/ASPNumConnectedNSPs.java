package simternet.ecj.terminals;

import simternet.application.ApplicationProvider;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasApplicationProvider;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class ASPNumConnectedNSPs extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		HasApplicationProvider hap = (HasApplicationProvider) problem;
		ApplicationProvider asp = hap.getApplicationProvider();

		DoubleGP dd = (DoubleGP) input;
		dd.value = asp.getNumConnectedNetworks();

	}

	@Override
	public String toString() {
		return "ASPNumConnectedNSPs()";
	}

}
