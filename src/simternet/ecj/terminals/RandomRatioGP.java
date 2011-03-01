package simternet.ecj.terminals;

import simternet.ecj.DoubleGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.ERC;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Code;
import ec.util.DecodeReturn;

public class RandomRatioGP extends ERC {

	private static final long	serialVersionUID	= 1L;
	private double				value;

	@Override
	public boolean decode(DecodeReturn dret) {
		int pos = dret.pos;
		String data = dret.data;
		Code.decode(dret);
		if (dret.type != DecodeReturn.T_DOUBLE) {
			dret.data = data;
			dret.pos = pos;
			return false;
		}
		this.value = dret.d;
		return true;
	}

	@Override
	public String encode() {
		return Code.encode(this.value);
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		DoubleGP result = (DoubleGP) input;
		result.value = this.value;
	}

	@Override
	public void mutateERC(EvolutionState state, int thread) {
		double v;
		do
			v = this.value + state.random[thread].nextGaussian() * 0.01;
		while ((v < 0.0) || (v >= 1.0));
		this.value = v;
	}

	@Override
	public boolean nodeEquals(GPNode node) {
		return ((node.getClass() == this.getClass()) && (((RandomRatioGP) node).value == this.value));
	}

	@Override
	public void resetNode(EvolutionState state, int thread) {
		this.value = state.random[thread].nextDouble();
	}

	@Override
	public String toString() {
		return this.name() + "[" + this.value + "]";
	}

}
