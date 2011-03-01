package simternet.ecj.operators;

import simternet.ecj.BooleanGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * OrGP returns true if either of its children are true. Otherwise, both of its
 * children are false, and it returns false.
 * 
 * @author kkoning
 * 
 */
public class OrGP extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;
		result.value = false;

		this.children[0].eval(state, thread, result, stack, individual, problem);
		if (result.value) // if one returns true
			return; // we don't need to evaluate the other, and the result
		// already contains true

		// if we're here, the first child returned false
		this.children[1].eval(state, thread, result, stack, individual, problem);

		// if the second child returns false, both are false, and the OR returns
		// false, result will already contain that. likewise, if second child
		// returns true, one child is true, so the OR returns true, and the
		// result already contains that.
	}

	@Override
	public String toString() {
		return "or";
	}

}
