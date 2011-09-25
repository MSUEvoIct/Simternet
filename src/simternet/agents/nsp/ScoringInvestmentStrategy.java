package simternet.agents.nsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sim.util.Int2D;
import simternet.engine.LocationIterator;
import simternet.network.EdgeNetwork;
import simternet.network.SimpleEdgeNetwork;

public class ScoringInvestmentStrategy implements InvestmentStrategy, Serializable {
	private static final long						serialVersionUID	= 1L;

	/**
	 * This is the lowest score at which a service provider will actually build
	 * a network.
	 */
	protected Double								buildThreshold;
	protected List<Class<? extends EdgeNetwork>>	networkTypes;
	protected NetworkProvider						nsp;
	protected List<PotentialNetwork>				potentialNetworks;

	/**
	 * 
	 * 
	 * @param nsp
	 * @param networkTypes
	 *            If no list of network types is specified, only
	 *            SimpleEdgeNetwork will be considered.
	 * @param buildThreshold
	 *            Only networks with at least this score will be built.
	 */
	public ScoringInvestmentStrategy(NetworkProvider nsp, List<Class<? extends EdgeNetwork>> networkTypes,
			Double buildThreshold) {
		this.nsp = nsp;
		this.buildThreshold = buildThreshold;
		if (networkTypes == null) {
			networkTypes = new ArrayList<Class<? extends EdgeNetwork>>();
			networkTypes.add(SimpleEdgeNetwork.class);
		}
		this.networkTypes = networkTypes;
		potentialNetworks = new ArrayList<PotentialNetwork>();
		populatePotentialNetworks();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void makeNetworkInvestment() {
		Double availableFinancing = nsp.financials.getAvailableFinancing();
		List<PotentialNetwork> networksBuilt = new ArrayList<PotentialNetwork>();
		updateScores();
		Collections.sort(potentialNetworks);

		for (PotentialNetwork pn : potentialNetworks) {
			if (pn.cost > availableFinancing) {
				break;
			}
			if (pn.score < 0) {
				break;
			}
			nsp.buildNetwork(pn.networkType, pn.location);
			availableFinancing -= pn.cost;
			networksBuilt.add(pn);
		}

		potentialNetworks.removeAll(networksBuilt);

	}

	private void populatePotentialNetworks() {
		for (Class<? extends EdgeNetwork> networkType : networkTypes) {
			for (Int2D location : new LocationIterator(nsp.s)) {
				potentialNetworks.add(new PotentialNetwork(nsp, networkType, location));
			}
		}
	}

	/**
	 * The standard method just uses a simplistic score for testing purposes.
	 * 
	 * @param pn
	 */
	protected void scoreSimpleNetwork(PotentialNetwork pn) {
		Double score = 0.0;

		Double populationWeight = 8.0;
		Double costWeight = 0.0001;
		Double distanceWeight = 10.0;

		// population. This is all people, not just those we specifically
		// know will demand SimpleNetwork.
		score += Math.pow(nsp.s.getPopulation(pn.location), 1.5) / nsp.s.config.consumerPopulationMax
				* populationWeight;
		score -= pn.cost * costWeight;
		score -= pn.distanceFromHome * distanceWeight;

		// divide score by the number of this type of network that would at the
		// potential location. (including this one)
		// score = score
		// / (this.nsp.simternet.getNumNetworks(pn.networkType,
		// pn.location) + 1);

		pn.score = score;
	}

	protected void updateScores() {
		for (PotentialNetwork pn : potentialNetworks)
			if (pn.networkType.equals(SimpleEdgeNetwork.class)) {
				scoreSimpleNetwork(pn);
			} else {
				pn.score = Double.NEGATIVE_INFINITY;
			}
	}

}
