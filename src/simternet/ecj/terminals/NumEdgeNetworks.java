package simternet.ecj.terminals;

import sim.util.Int2D;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasLocation;
import simternet.ecj.problems.HasSimternet;
import simternet.engine.Simternet;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class NumEdgeNetworks extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		HasLocation hl = (HasLocation) problem;
		Int2D location = hl.location();
		Simternet s = ((HasSimternet) problem).getSimternet();

		DoubleGP dd = (DoubleGP) input;
		int numNets = s.getNumNetworkProviders(location);
		dd.value = numNets;

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "NumEdgeNetworks";
	}

}
