package simternet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;

@SuppressWarnings("serial")
public abstract class AbstractConsumer implements Steppable {

	/**
	 * The # of individuals in this consumer class in each landscape pixel.
	 */
	protected DoubleGrid2D population;
	protected Double totalPopulationCached;
	protected boolean totalPopulationCacheDirty = true;
	protected Simternet s;

	/**
	 * @param s
	 * @param pd
	 * 
	 *            Create a new consumer class with the specified population
	 *            distribution.
	 */
	protected AbstractConsumer(Simternet s, PopulationDistribution pd) {
		this.s = s;
		population = new DoubleGrid2D(Exogenous.landscapeX,
				Exogenous.landscapeY, 0.0);
		initPopulation(pd);
	}

	/**
	 * @param s
	 * 
	 *            Create a new consumer class with the default population
	 *            distribution.
	 * 
	 */
	protected AbstractConsumer(Simternet s) {
		this(s, null);
	}

	/**
	 * @param cl
	 * @param price
	 * @param x
	 * @param y
	 * @return The total number of customers at x,y which would subscribe to the
	 *         specified network at the specified price. This specifies the
	 *         total market demand, not how that demand is allocated among
	 *         providers.
	 */
	@SuppressWarnings("unchecked")
	protected abstract Double demand(Class cl, Double price, Integer x,
			Integer y);

	protected void allocate() {
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				allocateAt(x, y);
	}

	protected abstract void allocateAt(Integer x, Integer y);

	@SuppressWarnings("unchecked")
	protected Double getSubscribers(Class network, AbstractNetworkProvider nsp,
			Integer x, Integer y) {
		return nsp.getCustomers(network, this, x, y);
	}

	/**
	 * @param pd
	 * 
	 *            Initialize the population distribution using the specified
	 *            method. If none is specified, use
	 *            Exogenous.defaultPopulationDistribution.
	 */
	protected void initPopulation(PopulationDistribution pd) {
		if (pd == null) {
			pd = Exogenous.defaultPopulationDistribution;
		}

		switch (pd) {
		case RANDOM_FLAT: {
			for (int x = 0; x < Exogenous.landscapeX; x++)
				for (int y = 0; y < Exogenous.landscapeY; y++)
					population.field[x][y] = s.random.nextDouble()
							* Exogenous.maxPopulation;
		}
		}
	}

	/**
	 * @param x
	 * @param y
	 * @return The population of this specific ConsumerClass at the target
	 *         location.
	 */
	public Double getPopulation(Integer x, Integer y) {
		return population.field[x][y];
	}

	/**
	 * @return The TOTAL population of this specific Consumer Class at ALL
	 *         locations.
	 */
	public Double getPopultation() {
		if (!totalPopulationCacheDirty) {
			return totalPopulationCached;
		}
		Double pop = new Double(0);
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				pop += getPopulation(x, y);
		totalPopulationCached = pop;
		totalPopulationCacheDirty = false;
		return pop;
	}

	protected void setPopulation(int x, int y, Double pop) {
		population.field[x][y] = pop;
		totalPopulationCacheDirty = true;
	}

	public void step(SimState state) {
		allocate();
	}

	public Map<AbstractNetworkProvider, Double> getPrices(Class network,
			Integer x, Integer y) {
		Map<AbstractNetworkProvider, Double> prices = new TreeMap<AbstractNetworkProvider, Double>();
		for (AbstractNetworkProvider nsp : s.getNspClasses()) {
			prices.put(nsp, nsp.getPrice(network, this, x, y));
		}

		return prices;
	}

}
