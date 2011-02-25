package simternet.ecj.terminals;

import simternet.ecj.problems.HasEdgeNetwork;
import simternet.network.AbstractEdgeNetwork;
import ec.EvolutionState;
import ec.Problem;
import ec.app.tutorial4.DoubleData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class NumEdgeSubscribers extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		HasEdgeNetwork hen = (HasEdgeNetwork) problem;
		AbstractEdgeNetwork aen = hen.getEdgeNetwork();

		Double numCustomers = aen.getNumSubscribers();

		DoubleData dd = (DoubleData) input;
		dd.x = numCustomers;

	}

	@Override
	public String toString() {
		return "edge.numSubscribers()";
	}

}
