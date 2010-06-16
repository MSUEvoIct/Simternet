package simternet.nsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import simternet.network.AbstractNetwork;
import simternet.network.SimpleEdgeNetwork;

public class ScoringInvestmentStrategy implements InvestmentStrategy,
		Serializable {

	@SuppressWarnings("unchecked")
	private class PotentialNetwork implements Serializable, Comparable {
		private static final long serialVersionUID = 1L;

		private Double cost;

		private Double distanceFromHome;
		private final Int2D location;
		private final Class<? extends AbstractNetwork> networkType;
		private Double score;

		public PotentialNetwork(Int2D location,
				Class<? extends AbstractNetwork> networkType) {
			this.networkType = networkType;
			this.location = location;

			try {
				AbstractNetwork an = networkType.newInstance();
				an.init(ScoringInvestmentStrategy.this.nsp, this.location);
				this.cost = an.getBuildCost();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
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
	protected SparseGrid2D networks;
	protected List<Class<? extends AbstractNetwork>> networkTypes;

	protected AbstractNetworkProvider nsp;

	protected List<PotentialNetwork> potentialNetworks;

	public ScoringInvestmentStrategy(AbstractNetworkProvider nsp,
			SparseGrid2D networks, Double buildThreshold,
			List<Class<? extends AbstractNetwork>> networkTypes) {
		this.nsp = nsp;
		this.networks = networks;
		this.buildThreshold = buildThreshold;
		if (networkTypes == null) {
			networkTypes = new ArrayList<Class<? extends AbstractNetwork>>();
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
		for (Class<? extends AbstractNetwork> networkType : this.networkTypes)
			for (int x = 0; x < this.nsp.simternet.parameters.x(); x++)
				for (int y = 0; y < this.nsp.simternet.parameters.y(); y++)
					this.potentialNetworks.add(new PotentialNetwork(new Int2D(
							x, y), networkType));
	}

	private void scoreSimpleNetwork(PotentialNetwork pn) {
		Double score = 0.0;

		Double populationWeight = 8.0;
		Double costWeight = 0.0001;
		Double distanceWeight = 10.0;

		// population. This is all people, not just those we specifically
		// know will demand SimpleNetwork.
		score += Math.pow(this.nsp.simternet.getPopulation(pn.location), 1.5)
				/ Double.parseDouble(this.nsp.simternet.parameters
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
