package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasBackboneLink;
import simternet.network.BackboneLink;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class BackboneCongestionRatio extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		HasBackboneLink hb = (HasBackboneLink) problem;
		BackboneLink link = hb.getBackboneLink();

		DoubleGP dd = (DoubleGP) input;
		if (link != null)
			dd.value = link.getCongestionAlgorithm().getCongestionRatio();
		else
			dd.value = 0.0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "BackbonCongestionRatio()";
	}

}
