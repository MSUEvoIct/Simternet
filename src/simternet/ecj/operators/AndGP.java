package simternet.ecj.operators;

import simternet.ecj.BooleanGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * AndGP returns true if both of its children are true. Otherwise, it returns
 * false
 * 
 * @author kkoning
 * 
 */
public class AndGP extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		BooleanGP result = (BooleanGP) input;

		this.children[0].eval(state, thread, result, stack, individual, problem);

		// if the first child is false, there's no way we can be true
		if (!result.value)
			return; // result will already contain false, return it

		// only here if the first child returned true
		this.children[1].eval(state, thread, result, stack, individual, problem);

		// so if second child returns true, the AND is true, and if the second
		// child returns false, the AND is false. So just pass on the second
		// child's result

	}

	@Override
	public String toString() {
		return "and";
	}

}
