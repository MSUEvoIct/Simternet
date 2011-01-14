package simternet.nsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sim.util.Int2D;
import simternet.LocationIterator;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.SimpleEdgeNetwork;

public class ScoringInvestmentStrategy implements InvestmentStrategy,
		Serializable {

	@SuppressWarnings("unchecked")
	protected class PotentialNetwork implements Serializable, Comparable {
		protected static final long serialVersionUID = 1L;

		protected Double cost;
		protected Double distanceFromHome;
		protected final Int2D location;
		protected final Class<? extends AbstractEdgeNetwork> networkType;
		protected Double score;

		public PotentialNetwork(
				Class<? extends AbstractEdgeNetwork> networkType, Int2D location) {
			this.networkType = networkType;
			this.location = location;

			this.cost = AbstractEdgeNetwork.getBuildCost(networkType,
					ScoringInvestmentStrategy.this.nsp, location);
			this.distanceFromHome = ScoringInvestmentStrategy.this.nsp
					.getHomeBase().distance(location);

			this.score = Double.NEGATIVE_INFINITY;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 * 
		 * Comparisons are backwards because we want a list sorted in descending
		 * order
		 */
		@Override
		public int compareTo(Object o) {
			PotentialNetwork pn = (PotentialNetwork) o;
			if (this.score > pn.score)
				return -1;
			if (this.score < pn.score)
				return 1;
			return 0;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is the lowest score at which a service provider will actually build
	 * a network.
	 */
	protected Double buildThreshold;
	protected List<Class<? extends AbstractEdgeNetwork>> networkTypes;
	protected AbstractNetworkProvider nsp;
	protected List<PotentialNetwork> potentialNetworks;

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
	public ScoringInvestmentStrategy(AbstractNetworkProvider nsp,
			List<Class<? extends AbstractEdgeNetwork>> networkTypes,
			Double buildThreshold) {
		this.nsp = nsp;
		this.buildThreshold = buildThreshold;
		if (networkTypes == null) {
			networkTypes = new ArrayList<Class<? extends AbstractEdgeNetwork>>();
			networkTypes.add(SimpleEdgeNetwork.class);
		}
		this.networkTypes = networkTypes;
		this.potentialNetworks = new ArrayList<PotentialNetwork>();
		this.populatePotentialNetworks();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void makeNetworkInvestment() {
		Double availableFinancing = this.nsp.financials.getAvailableFinancing();
		List<PotentialNetwork> networksBuilt = new ArrayList<PotentialNetwork>();
		this.updateScores();
		Collections.sort(this.potentialNetworks);

		for (PotentialNetwork pn : this.potentialNetworks) {
			if (pn.cost > availableFinancing)
				break;

			this.nsp.buildNetwork(pn.networkType, pn.location);
			availableFinancing -= pn.cost;
			networksBuilt.add(pn);
		}

		this.potentialNetworks.removeAll(networksBuilt);

	}

	private void populatePotentialNetworks() {
		for (Class<? extends AbstractEdgeNetwork> networkType : this.networkTypes)
			for (Int2D location : new LocationIterator(this.nsp.simternet))
				this.potentialNetworks.add(new PotentialNetwork(networkType,
						location));
	}

	private void scoreSimpleNetwork(PotentialNetwork pn) {
		Double score = 0.0;

		Double populationWeight = 8.0;
		Double costWeight = 0.0001;
		Double distanceWeight = 10.0;

		// population. This is all people, not just those we specifically
		// know will demand SimpleNetwork.
		score += Math.pow(this.nsp.simternet.getPopulation(pn.location), 1.5)
				/ Double.parseDouble(this.nsp.simternet.config
						.getProperty("landscape.population.max"))
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

	private void updateScores() {
		for (PotentialNetwork pn : this.potentialNetworks)
			if (pn.networkType.equals(SimpleEdgeNetwork.class))
				this.scoreSimpleNetwork(pn);
			else
				pn.score = Double.NEGATIVE_INFINITY;
	}

}
