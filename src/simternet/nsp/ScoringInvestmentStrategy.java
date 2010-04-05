package simternet.nsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import simternet.Exogenous;
import simternet.network.AbstractNetwork;
import simternet.network.SimpleNetwork;

public class ScoringInvestmentStrategy implements InvestmentStrategy {

	protected AbstractNetworkProvider nsp;
	protected SparseGrid2D networks;
	protected List<PotentialNetwork> potentialNetworks;
	protected List<Class<? extends AbstractNetwork>> networkTypes;

	/**
	 * This is the lowest score at which a service provider will actually build
	 * a network.
	 */
	protected Double buildThreshold;

	@SuppressWarnings("unchecked")
	private class PotentialNetwork implements Comparable {
		public PotentialNetwork(Integer x, Integer y,
				Class<? extends AbstractNetwork> networkType) {
			this.locationX = x;
			this.locationY = y;
			this.networkType = networkType;
			
			try {
				AbstractNetwork an = networkType.newInstance();
				an.init(nsp, locationX, locationY);
				this.cost = an.getBuildCost();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
			this.distanceFromHome = nsp.getHomeBase().distance(this.locationX, this.locationY);

			this.score = Double.NEGATIVE_INFINITY;
		}

		private final Integer locationX;
		private final Integer locationY;
		private final Class<? extends AbstractNetwork> networkType;
		private Double cost;
		private Double score;
		private Double distanceFromHome;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 * 
		 * Comparisons are backwards because we want a list sorted in descending order
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

	public ScoringInvestmentStrategy(AbstractNetworkProvider nsp,
			SparseGrid2D networks, Double buildThreshold,
			List<Class<? extends AbstractNetwork>> networkTypes) {
		this.nsp = nsp;
		this.networks = networks;
		this.buildThreshold = buildThreshold;
		if (networkTypes == null) {
			networkTypes = new ArrayList<Class<? extends AbstractNetwork>>();
			networkTypes.add(SimpleNetwork.class);
		}
		this.networkTypes = networkTypes;
		this.potentialNetworks = new ArrayList<PotentialNetwork>();
		
		populatePotentialNetworks();
		
	}

	private void populatePotentialNetworks() {
		for (Class<? extends AbstractNetwork> networkType : networkTypes)
			for (int x = 0; x < Exogenous.landscapeX; x++)
				for (int y = 0; y < Exogenous.landscapeY; y++)
					this.potentialNetworks.add(new PotentialNetwork(x,y,networkType));
	}
	
	private void updateScores() {
		for (PotentialNetwork pn : potentialNetworks) {
			// recalculate costs?  Only if they might change for some reason...
			
			if (pn.networkType.equals(SimpleNetwork.class))
				scoreSimpleNetwork(pn);
			else
				pn.score = Double.NEGATIVE_INFINITY;
		}
	}
	
	
	private void scoreSimpleNetwork(PotentialNetwork pn) {
		Double score = 0.0;
		
		Double populationWeight = 8.0;
		Double costWeight = 0.0001;
		Double distanceWeight = 10.0;
		
		// population.  This is all people, not just those we specifically
		// know will demand SimpleNetwork.
		score += Math.pow(nsp.simternet.getPopulation(pn.locationX, pn.locationY), 1.5) / Exogenous.maxPopulation * populationWeight;
		score -= pn.cost * costWeight;
		score -= pn.distanceFromHome * distanceWeight;
		
		// divide score by the number of this type of network that would at the potential location. (including this one)
		score = score / (nsp.simternet.getNumNetworks(pn.networkType, pn.locationX, pn.locationY) + 1);
		
		
		pn.score = score;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void makeNetworkInvestment() {
		Double availableFinancing = nsp.investor.getAvailableFinancing();
		List<PotentialNetwork> networksBuilt = new ArrayList<PotentialNetwork>();
		updateScores();
		Collections.sort(this.potentialNetworks);
		
		for (PotentialNetwork pn : potentialNetworks) {
			if (pn.cost > availableFinancing) 
				break;
		
			nsp.buildNetwork(pn.networkType, pn.locationX, pn.locationY);
			availableFinancing -= pn.cost;
			networksBuilt.add(pn);
		}
		
		this.potentialNetworks.removeAll(networksBuilt);
		
	}

}
