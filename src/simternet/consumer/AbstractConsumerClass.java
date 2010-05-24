package simternet.consumer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import simternet.PopulationDistribution;
import simternet.Simternet;
import simternet.network.AbstractNetwork;
import simternet.nsp.AbstractNetworkProvider;

@SuppressWarnings("serial")
public abstract class AbstractConsumerClass implements Steppable, Serializable {

	protected Set<Class<? extends AbstractNetwork>> networkTypesDemanded = new HashSet<Class<? extends AbstractNetwork>>();

	/**
	 * The # of individuals in this consumer class in each landscape pixel.
	 */
	protected DoubleGrid2D population;
	protected Simternet s;
	/**
	 * Total # of consumers of this class who subscribe to the network type at
	 * each location (all providers)
	 */
	private Map<Class<? extends AbstractNetwork>, DoubleGrid2D> totalLocalSubscriptionsCache;
	private Map<Class<? extends AbstractNetwork>, boolean[][]> totalLocalSubscriptionsCacheDirty;

	// To Do: Add some cache variables here...?

	protected Double totalPopulationCached;
	protected boolean totalPopulationCacheDirty = true;
	/**
	 * Total # of consumers in this class who subscribe to each network
	 */
	protected Map<Class<? extends AbstractNetwork>, Double> totalSubscriptions;

	/**
	 * @param s
	 * 
	 *            Create a new consumer class with the default population
	 *            distribution.
	 * 
	 */
	protected AbstractConsumerClass(Simternet s) {
		this(s, null);
	}

	/**
	 * @param s
	 * @param pd
	 * 
	 *            Create a new consumer class with the specified population
	 *            distribution.
	 */
	protected AbstractConsumerClass(Simternet s, PopulationDistribution pd) {
		this.s = s;
		this.population = new DoubleGrid2D(s.parameters.x(), s.parameters.y(),
				0.0);
		this.initPopulation(pd);
		this.totalSubscriptions = new HashMap<Class<? extends AbstractNetwork>, Double>();
		this.totalLocalSubscriptionsCache = new HashMap<Class<? extends AbstractNetwork>, DoubleGrid2D>();
		this.totalLocalSubscriptionsCacheDirty = new HashMap<Class<? extends AbstractNetwork>, boolean[][]>();

		for (Class<? extends AbstractNetwork> an : this.networkTypesDemanded) {
			this.totalSubscriptions.put(an, 0.0);
			this.totalLocalSubscriptionsCache.put(an, new DoubleGrid2D(
					s.parameters.x(), s.parameters.y(), 0.0));
			boolean[][] veryDirtyCache = new boolean[s.parameters.x()][s.parameters
					.y()];
			for (int x = 0; x < s.parameters.x(); x++)
				for (int y = 0; y < s.parameters.y(); y++)
					veryDirtyCache[x][y] = true;
			this.totalLocalSubscriptionsCacheDirty.put(an, veryDirtyCache);

		}
	}

	/**
	 * @param an
	 * @param price
	 * @param x
	 * @param y
	 * @return The total number of customers at x,y which would subscribe to the
	 *         specified network at the specified price. This specifies the
	 *         total market demand, not how that demand is allocated among
	 *         providers.
	 */
	protected abstract Double demand(Class<? extends AbstractNetwork> an,
			Double price, Integer x, Integer y);

	protected Double getNumSubscriptions(Class<? extends AbstractNetwork> n,
			AbstractNetworkProvider nsp, Integer x, Integer y) {

		AbstractNetwork an = nsp.getNetworkAt(n, x, y);
		if (an == null) // provider has no network, we can't subscribe to it
			return 0.0;

		return an.getCustomers(this);
	}

	/**
	 * @param x
	 * @param y
	 * @return The population of this specific ConsumerClass at the target
	 *         location.
	 */
	public Double getPopulation(Integer x, Integer y) {
		return this.population.field[x][y];
	}

	/**
	 * @return The TOTAL population of this specific Consumer Class at ALL
	 *         locations.
	 */
	public Double getPopultation() {
		if (!this.totalPopulationCacheDirty)
			return this.totalPopulationCached;
		Double pop = new Double(0);
		for (int x = 0; x < this.s.parameters.x(); x++)
			for (int y = 0; y < this.s.parameters.y(); y++)
				pop += this.getPopulation(x, y);
		this.totalPopulationCached = pop;
		this.totalPopulationCacheDirty = false;
		return pop;
	}

	public Double getTotalLocalSubscriptions(
			Class<? extends AbstractNetwork> an, Integer x, Integer y) {
		Set<AbstractNetworkProvider> nsps = this.s.getNetworkServiceProviders();
		double runningTotal = 0.0;

		for (AbstractNetworkProvider nsp : nsps) {
			Double foo = this.getNumSubscriptions(an, nsp, x, y);
			if (foo != null)
				runningTotal += foo;
		}

		return runningTotal;
	}

	protected abstract void initNetData();

	/**
	 * @param pd
	 * 
	 *            Initialize the population distribution using the specified
	 *            method. If none is specified, use
	 *            Exogenous.defaultPopulationDistribution.
	 * 
	 *            To Do: Consider (static) factory pattern?
	 */
	protected void initPopulation(PopulationDistribution pd) {
		// TODO: Have way of parameterizing population distribution based on
		// properties.
		if (pd == null)
			pd = PopulationDistribution.RANDOM_FLAT;

		switch (pd) {
		case RANDOM_FLAT: {
			for (int x = 0; x < this.s.parameters.x(); x++)
				for (int y = 0; y < this.s.parameters.y(); y++)
					this.population.field[x][y] = this.s.random.nextDouble()
							* Double.parseDouble(this.s.parameters
									.getProperty("landscape.population.max"));
		}
		}
	}

	protected abstract void makeConsumptionDecisionAt(Integer x, Integer y);

	protected void makeConsumptionDecisions() {
		for (int x = 0; x < this.s.parameters.x(); x++)
			for (int y = 0; y < this.s.parameters.y(); y++)
				this.makeConsumptionDecisionAt(x, y);
	}

	/**
	 * @param n
	 * @param nsp
	 * @param x
	 * @param y
	 * @param numSubs
	 * 
	 *            Sets the actual consumption variables. These are stored in the
	 * 
	 */
	protected void setNumSubscriptions(Class<? extends AbstractNetwork> n,
			AbstractNetworkProvider nsp, Integer x, Integer y, Double numSubs) {
		AbstractNetwork an = nsp.getNetworkAt(n, x, y);
		if (an != null)
			an.setCustomers(this, numSubs);
	}

	protected void setPopulation(int x, int y, Double pop) {
		this.population.field[x][y] = pop;
		this.totalPopulationCacheDirty = true;
	}

	public void step(SimState state) {
		this.makeConsumptionDecisions();
		// System.out.println("Consumer");
	}

}
