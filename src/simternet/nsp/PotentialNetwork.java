package simternet.nsp;

import java.io.Serializable;

import sim.util.Int2D;
import simternet.network.EdgeNetwork;

public class PotentialNetwork implements Serializable, Comparable {
	protected static final long			serialVersionUID	= 1L;
	public Double						cost;
	public Double						distanceFromHome;
	public Int2D						location;
	public Class<? extends EdgeNetwork>	networkType;
	public Double						numCompetitors;
	public Double						population;
	public Double						score;

	public PotentialNetwork(NetworkProvider nsp, Class<? extends EdgeNetwork> networkType, Int2D location) {
		this.networkType = networkType;
		this.location = location;
		this.cost = EdgeNetwork.getBuildCost(networkType, nsp, location);
		this.distanceFromHome = nsp.getHomeBase().distance(location);
		this.population = nsp.simternet.getPopulation(location);
		this.numCompetitors = nsp.simternet.getNumNetworkProviders(location);

		this.score = Double.NEGATIVE_INFINITY;
	}

	@Override
	public int compareTo(Object o) {
		PotentialNetwork pn = (PotentialNetwork) o;
		if (this.score > pn.score)
			return -1;
		if (this.score < pn.score)
			return 1;
		return 0;
	}

	/**
	 * Update data to more accurately reflect conditions in the simulation.
	 * i.e., update the number of competitors, costs and population if that
	 * changes...
	 */
	public void updateDetails() {
		// TODO
	}

}