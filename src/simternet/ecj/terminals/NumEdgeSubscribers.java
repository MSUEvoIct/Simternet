package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasEdgeNetwork;
import simternet.network.EdgeNetwork;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * This GP terminal requires that problem implements the HasEdgeNetwork
 * interface, which it uses to determine the number of current individual
 * subscribers.
 * 
 * @author kkoning
 * 
 */
public class NumEdgeSubscribers extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		HasEdgeNetwork hen = (HasEdgeNetwork) problem;
		EdgeNetwork aen = hen.getEdgeNetwork();

		DoubleGP dd = (DoubleGP) input;
		dd.value = aen.getNumSubscribers();

	}

	@Override
	public String toString() {
		return "edge.numSubscribers()";
	}

}
