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

public class LowestEdgePrice extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		Int2D location = ((HasLocation) problem).location();
		Simternet s = ((HasSimternet) problem).getSimternet();

		double lowestPrice = s.marketInfo.cheapestEdgeNetwork(location);
		DoubleGP dd = (DoubleGP) input;
		dd.value = lowestPrice;

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MinEdgePrice";
	}

}
