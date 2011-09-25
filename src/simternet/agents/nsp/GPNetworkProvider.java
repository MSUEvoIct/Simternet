package simternet.agents.nsp;

import java.util.ArrayList;
import java.util.List;


import sim.engine.SimState;
import simternet.agents.asp.ApplicationProvider;
import simternet.ecj.EvolvableAgent;
import simternet.ecj.SimternetGPIndividual;
import simternet.engine.Simternet;
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
		investmentStrategy = new ScoringInvestmentStrategy(this, edgeTypes, 0.0);
	}

	@Override
	public Double getFitness() {
		// TODO: Think about the proper fitness measure.
		double netWorth = financials.getNetWorth();
		if (netWorth > 0)
			return netWorth;
		else
			return 0.0;
	}

	@Override
	public Individual getIndividual() {
		return ind;
	}

	@Override
	public void setIndividual(Individual i) {
		ind = (SimternetGPIndividual) i;
		ind.setAgent(this);
		pricingStrategy = new GPEdgePricingStrategy(this, ind, ind.trees[0]);
		List<Class<? extends EdgeNetwork>> edgeTypes = new ArrayList<Class<? extends EdgeNetwork>>();
		edgeTypes.add(SimpleEdgeNetwork.class);
		investmentStrategy = new GPScoringInvestmentStrategy(this, edgeTypes, ind, ind.trees[1]);
		interconnectStrategy = new GPInterconnectPricingStrategy(this, ind, ind.trees[2]);
		edgeBackboneUpgradeStrategy = new GPEdgeBackboneUpgradeStrategy(this, ind, ind.trees[3]);
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		// TODO, Create generic strategy and put this code into
		// NetworkProvider.step()
		for (ApplicationProvider asp : s.getASPs()) {
			double price = interconnectStrategy.getASPTransitPrice(asp);
			aspTransitPrice.put(asp, price);
		}

	}

}
