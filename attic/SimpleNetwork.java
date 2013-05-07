package simternet.network;

import java.io.Serializable;

public class SimpleNetwork extends EdgeNetwork implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Double getBuildCost() {
		Double cost = 0.0;
		Double population = this.nsp.simternet.getPopulation(this.location);

		cost += Double.parseDouble(this.nsp.simternet.parameters
				.getProperty("networks.SimpleNetwork.cost.perArea"));
		cost += population
				* Double.parseDouble(this.nsp.simternet.parameters
						.getProperty("networks.SimpleNetwork.cost.perUser"));

		return cost;
	}

}
