package simternet.ecj.terminals;

import simternet.Financials;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.HasFinancials;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class AssetsLiquid extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		HasFinancials hf = (HasFinancials) problem;
		Financials f = hf.getFinancials();

		DoubleGP dd = (DoubleGP) input;
		dd.value = f.getAssetsLiquid();
	}

	@Override
	public String toString() {
		return "Financials.AssetsLiquid";
	}

}
