package simternet.nsp;

import simternet.Simternet;
import simternet.ecj.EvolvableAgent;
import simternet.ecj.SimternetGPIndividual;
import ec.Individual;

public class GPNetworkProvider extends NetworkProvider implements EvolvableAgent {

	private static final long		serialVersionUID	= 1L;
	protected SimternetGPIndividual	ind;

	public GPNetworkProvider(Simternet simternet) {
		super(simternet);
		this.investmentStrategy = new BuildEverywhereStrategy(this);
	}

	@Override
	public Double getFitness() {
		// TODO: Actually think about the proper fitness measure.
		return this.financials.getPresentValue();
	}

	@Override
	public Individual getIndividual() {
		return this.ind;
	}

	@Override
	public void setIndividual(Individual i) {
		this.ind = (SimternetGPIndividual) i;
		this.ind.setAgent(this);
		this.pricingStrategy = new GPPricingStrategy(this, this.ind, this.ind.trees[0]);
	}

}
