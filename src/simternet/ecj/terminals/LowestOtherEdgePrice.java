package simternet.ecj.terminals;

import sim.util.Int2D;
import simternet.agents.nsp.NetworkProvider;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasEdgeNetwork;
import simternet.ecj.problems.HasLocation;
import simternet.ecj.problems.HasSimternet;
import simternet.engine.Simternet;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class LowestOtherEdgePrice extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		Int2D location = ((HasLocation) problem).location();
		Simternet s = ((HasSimternet) problem).getSimternet();
		NetworkProvider nsp = ((HasEdgeNetwork) problem).getEdgeNetwork().getOwner();

		double lowestPrice = s.marketInfo.cheapestOtherEdgeNetwork(location, nsp);
		DoubleGP dd = (DoubleGP) input;
		dd.value = lowestPrice;

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MinOtherEdgePrice";
	}

}
