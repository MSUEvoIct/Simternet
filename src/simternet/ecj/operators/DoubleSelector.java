package simternet.ecj.operators;

import simternet.ecj.BooleanGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class DoubleSelector extends GPNode {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void checkConstraints(EvolutionState state, int tree, GPIndividual typicalIndividual,
			Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (this.children.length != 3)
			state.output.error("Incorrect number of children for node " + this.toStringForError());
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		BooleanGP condition = new BooleanGP();

		this.children[0].eval(state, thread, condition, stack, individual, problem);

		if (condition.value)
			this.children[1].eval(state, thread, input, stack, individual, problem);
		else
			this.children[2].eval(state, thread, input, stack, individual, problem);

	}

	@Override
	public String toString() {
		return "if";
	}

}
