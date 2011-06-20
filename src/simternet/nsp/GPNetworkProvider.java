package simternet.nsp;

import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.ecj.EvolvableAgent;
import simternet.ecj.SimternetGPIndividual;
import simternet.network.EdgeNetwork;
import simternet.network.SimpleEdgeNetwork;
import ec.Individual;

public class GPNetworkProvider extends NetworkProvider implements EvolvableAgent {

	private static final long		serialVersionUID	= 1L;
	protected SimternetGPIndividual	ind;

	public GPNetworkProvider(Simternet simternet) {
		super(simternet);
		List<Class<? extends EdgeNetwork>> edgeTypes = new ArrayList<Class<? extends EdgeNetwork>>();
		edgeTypes.add(SimpleEdgeNetwork.class);
		this.investmentStrategy = new ScoringInvestmentStrategy(this, edgeTypes, 0.0);
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
		this.pricingStrategy = new GPPricingStrategy(this, this.ind, this.ind.trees[0]);
		List<Class<? extends EdgeNetwork>> edgeTypes = new ArrayList<Class<? extends EdgeNetwork>>();
		edgeTypes.add(SimpleEdgeNetwork.class);
		this.investmentStrategy = new GPScoringInvestmentStrategy(this, edgeTypes, this.ind, this.ind.trees[1]);
		this.interconnectStrategy = new GPInterconnectPricingStrategy(this, this.ind, this.ind.trees[2]);
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		// TODO, Create generic strategy and put this code into
		// NetworkProvider.step()
		for (ApplicationProvider asp : this.simternet.getASPs()) {
			double price = this.interconnectStrategy.getASPTransitPrice(asp);
			this.aspTransitPrice.put(asp, price);
		}

	}

}
