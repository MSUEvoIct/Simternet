package simternet.application;

import simternet.Simternet;
import simternet.ecj.EvolvableAgent;
import simternet.ecj.SimternetGPIndividual;
import ec.Individual;

public class GPApplicationProvider extends ApplicationProvider implements EvolvableAgent {

	private static final long		serialVersionUID	= 1L;
	protected SimternetGPIndividual	ind;

	public GPApplicationProvider(Simternet s) {
		// XXX: FIX
		super(s, AppCategory.INFORMATION);
	}

	@Override
	public Double getFitness() {
		// TODO: Think about the proper fitness measure.
		return this.financials.getNetWorth();
	}

	@Override
	public Individual getIndividual() {
		return this.ind;
	}

	@Override
	public void setIndividual(Individual i) {
		this.ind = (SimternetGPIndividual) i;
		this.ind.setAgent(this);
		this.qualityStrategy = new GPQualityStrategy(this, this.ind, this.ind.trees[0]);
	}

}
