package simternet.consumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import simternet.PopulationDistribution;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;
import simternet.network.NetFlow;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.TemporalBitMap2D;
import simternet.temporal.TemporalHashMap;
import simternet.temporal.TemporalSparseGrid2D;

@SuppressWarnings("serial")
public abstract class AbstractConsumerClass implements Steppable, AsyncUpdate,
		Serializable {

	protected TemporalHashMap<ApplicationServiceProvider, TemporalBitMap2D> applicationServiceSubscriptions;
	protected TemporalSparseGrid2D networkSubscriptions;

	/**
	 * The # of individuals in this consumer class in each landscape pixel. If
	 * this changes during the simulation it will need to be made Temporal.
	 */
	protected DoubleGrid2D population;
	protected Simternet s;

	/**
	 * Create a new consumer class with randomly generated population and
	 * preferences.
	 * 
	 * @param s
	 *            Link back to the simulation
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
		this.applicationServiceSubscriptions = new TemporalHashMap<ApplicationServiceProvider, TemporalBitMap2D>();
		this.networkSubscriptions = new TemporalSparseGrid2D(s.parameters.x(),
				s.parameters.y());

		this.initPopulation(pd);

	}

	protected void consumeApplications() {
		for (Map.Entry<ApplicationServiceProvider, TemporalBitMap2D> aspMap : this.applicationServiceSubscriptions
				.entrySet()) {
			ApplicationServiceProvider asp = aspMap.getKey();
			TemporalBitMap2D subMap = aspMap.getValue();
			for (Int2D location : this.s.allLocations())
				if (subMap.get(location)) {
					NetFlow nf = new NetFlow();
					nf.amount = this.population.get(location.x, location.y);
					asp.processUsage(nf);
				}
		}
	}

	@SuppressWarnings("unchecked")
	protected void consumeNetworks() {
		Iterator<AbstractEdgeNetwork> i = this.networkSubscriptions.iterator();
		while (i.hasNext()) {
			AbstractEdgeNetwork net = i.next();
			net.receivePayment(this, this.getPopulation(net.getLocation()));
		}

	}

	public Collection<ApplicationServiceProvider> getASPSubscriptions(
			Int2D location) {
		Collection<ApplicationServiceProvider> asps = new ArrayList<ApplicationServiceProvider>();

		for (Map.Entry<ApplicationServiceProvider, TemporalBitMap2D> aspMap : this.applicationServiceSubscriptions
				.entrySet())
			if (aspMap.getValue().get(location))
				asps.add(aspMap.getKey());

		return asps;
	}

	public Double getPopulation(Int2D location) {
		return this.population.field[location.x][location.y];
	}

	/**
	 * If this function will be called often, it should be reduced from O=n^2 to
	 * O=1 by tracking population changes in a separate variable.
	 * 
	 * @return The TOTAL population of this specific Consumer Class at ALL
	 *         locations.
	 */
	public Double getPopultation() {
		double pop = 0.0;
		for (int x = 0; x < this.s.parameters.x(); x++)
			for (int y = 0; y < this.s.parameters.y(); y++)
				pop += this.population.get(x, y);
		return pop;
	}

	/**
	 * Initialize the population distribution using the specified method. If
	 * none is specified, use Exogenous.defaultPopulationDistribution.
	 * 
	 * @param pd
	 * 
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

	public boolean isSubscribed(AbstractEdgeNetwork network) {
		return this.networkSubscriptions.equals(network);
	}

	/**
	 * Make decisions about <i>which</i> applications to use. Process their
	 * actual usage in consumeApplications().
	 */
	protected abstract void manageApplications();

	protected abstract void manageNetworks();

	public Set<AbstractNetwork> networksSubscribed(Int2D location) {
		HashSet<AbstractNetwork> muffin = new HashSet<AbstractNetwork>();
		Bag bag = this.networkSubscriptions.getObjectsAtLocation(location.x,
				location.y);
		for (int i = 0; i < bag.numObjs; i++)
			muffin.add((AbstractNetwork) bag.objs[i]);
		return muffin;
	}

	public Double numSubscriptions(AbstractEdgeNetwork network) {
		Int2D location = this.networkSubscriptions.getObjectLocation(network);
		if (null == location)
			return 0.0;
		return this.getPopulation(location);
	}

	protected void setPopulation(Int2D location, Double pop) {
		this.population.field[location.x][location.y] = pop;
	}

	public void step(SimState state) {
		if (this.s.parameters.debugLevel() > 10)
			System.out.println("Stepping" + this.toString());

		// Make decisions about consumption
		this.manageNetworks();
		this.manageApplications();

		// Act on those decisions
		this.consumeNetworks();
		this.consumeApplications();
	}

	@Override
	public void update() {
		this.applicationServiceSubscriptions.update();
		this.networkSubscriptions.update();
	}

}
