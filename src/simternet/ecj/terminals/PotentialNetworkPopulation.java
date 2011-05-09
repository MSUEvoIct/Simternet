package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasPotentialNetwork;
import simternet.nsp.PotentialNetwork;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class PotentialNetworkPopulation extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		HasPotentialNetwork hpn = (HasPotentialNetwork) problem;
		PotentialNetwork pn = hpn.getPotentialNetwork();

		DoubleGP dd = (DoubleGP) input;
		dd.value = pn.population;

	}

	@Override
	public String toString() {
		return "PotentialNetwork.population";
	}

}
