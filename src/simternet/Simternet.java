package simternet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import simternet.application.AppCategory;
import simternet.application.ApplicationServiceProvider;
import simternet.consumer.AbstractConsumerClass;
import simternet.consumer.ApplicationOptimizer;
import simternet.consumer.NetworkServiceMiser;
import simternet.network.AbstractNetwork;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.DumbNetworkServiceProvider;
import simternet.nsp.EvolvingNetworkProvider;
import simternet.temporal.Arbiter;

/**
 * This is the "root" class of the simulation. See the MASON documentation for
 * more details.
 * 
 * @author kkoning
 */
public class Simternet extends SimState implements Serializable {

	/*
	 * Do we use an LCS to evolve the NSP?
	 */
	private static boolean		evolve				= false;

	/**
	 * Storing a version identifier is appropriate for this class, as we will
	 * likely be saving it often and may want to read older versions in a
	 * predictable, specified way.
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public static void main(String[] args) {
		for (String s : args)
			if (s.equals("--evolve"))
				Simternet.evolve = true;
		SimState.doLoop(Simternet.class, args);
		System.exit(0);
	}

	/**
	 * All application service providers in the simulation
	 */
	protected Collection<ApplicationServiceProvider>					applicationServiceProviders;

	protected Map<AppCategory, Collection<ApplicationServiceProvider>>	ASPsByCategory;

	public Parameters													config;

	/**
	 * All consumer classes in the simulation.
	 */
	protected SparseGrid2D												consumerClasses;

	/**
	 * All Network Service Providers in the simulation.
	 */
	protected Collection<AbstractNetworkProvider>						networkServiceProviders;

	public Simternet(long seed) {
		this(seed, null);
	}

	public Simternet(long seed, Parameters config) {
		super(seed);
		if (config != null)
			this.config = config;
		else
			this.config = new Parameters();
	}

	/**
	 * A convienence method providing an iterator for all locations.
	 * 
	 * @return An iterator of all locations as Int2D objects.
	 */
	public Iterable<Int2D> allLocations() {
		return new Iterable<Int2D>() {
			@Override
			public Iterator<Int2D> iterator() {
				return new LocationIterator(Simternet.this.config.x(), Simternet.this.config.y());
			}
		};
	}

	/**
	 * TODO:
	 * 
	 * This function should be called whenever a consumer agent becomes active.
	 * It should take care of adding it to all the appropriate data structures
	 * and the MASON schedule.
	 * 
	 * @param nsp
	 */
	public void enterMarket(AbstractConsumerClass acc) {
		this.consumerClasses.setObjectLocation(acc, acc.getLocation());
		this.schedule.scheduleRepeating(Schedule.EPOCH, 12, acc);
	}

	/**
	 * This function should be called whenever a network service provider
	 * becomes active. It should take care of adding it to all the appropriate
	 * data structures and the MASON schedule.
	 * 
	 * @param nsp
	 */
	public void enterMarket(AbstractNetworkProvider nsp) {
		this.networkServiceProviders.add(nsp);
		this.schedule.scheduleRepeating(Schedule.EPOCH, 1, nsp);
	}

	/**
	 * TODO:
	 * 
	 * This function should be called whenever an application service provider
	 * becomes active. It should take care of adding it to all the appropriate
	 * data structures and the MASON schedule.
	 * 
	 * @param asp
	 */
	public void enterMarket(ApplicationServiceProvider asp) {
		this.applicationServiceProviders.add(asp);
		Collection<ApplicationServiceProvider> appsInCategory = this.ASPsByCategory.get(asp.getAppCategory());
		if (appsInCategory == null) {
			appsInCategory = new ArrayList<ApplicationServiceProvider>();
			this.ASPsByCategory.put(asp.getAppCategory(), appsInCategory);
		}

		appsInCategory.add(asp);

		this.schedule.scheduleRepeating(asp);
	}

	@Override
	public void finish() {
		super.finish();

		List<AbstractNetwork> nets = new ArrayList<AbstractNetwork>();

		for (ApplicationServiceProvider asp : this.applicationServiceProviders)
			nets.add(asp.getDataCenter());

		for (AbstractNetworkProvider nsp : this.networkServiceProviders) {
			nets.add(nsp.getBackboneNetwork());
			for (AbstractNetwork aen : nsp.getEdgeNetworks())
				nets.add(aen);
		}

		NetworkGraphDataOutput ngdo = new NetworkGraphDataOutput(this, nets);

		ngdo.output();

	}

	/**
	 * This is only used by the user interface, and therefore efficiency is not
	 * a priority
	 * 
	 * @return A grid containing the number of active subscribers in each
	 *         square.
	 */
	public DoubleGrid2D getAllActiveSubscribersGrid() {
		final Double initValue = 0.0;
		DoubleGrid2D ret = new DoubleGrid2D(this.config.x(), this.config.y(), initValue);

		for (AbstractNetworkProvider nsp : this.networkServiceProviders)
			for (int i = 0; i < ret.getWidth(); i++)
				for (int j = 0; j < ret.getHeight(); j++)
					ret.set(i, j, ret.get(i, j) + nsp.getCustomers(new Int2D(i, j)));
		return ret;
	}

	public Collection<ApplicationServiceProvider> getASPs() {
		return this.applicationServiceProviders;
	}

	public Collection<ApplicationServiceProvider> getASPs(AppCategory c) {
		Collection<ApplicationServiceProvider> asps = this.ASPsByCategory.get(c);
		if (asps == null) {
			asps = new ArrayList<ApplicationServiceProvider>();
			this.ASPsByCategory.put(c, asps);
		}
		return asps;
	}

	public SparseGrid2D getConsumerClasses() {
		return this.consumerClasses;
	}

	public DoubleGrid2D getMyActiveSubscribersGrid(AbstractNetworkProvider np) {
		final Double initValue = 0.0;
		DoubleGrid2D ret = new DoubleGrid2D(this.config.x(), this.config.y(), initValue);
		for (AbstractNetworkProvider nsp : this.networkServiceProviders)
			if (this.networkServiceProviders == np)
				for (int i = 0; i < ret.getWidth(); i++)
					for (int j = 0; j < ret.getHeight(); j++) {
						ret.set(i, j, ret.get(i, j) + nsp.getCustomers(new Int2D(i, j)));
						System.out.println(ret.get(i, j));
					}
		return ret;
	}

	/**
	 * TODO: This is a relatively expensive function. Watch and investigate
	 * caching if this becomes a problem.
	 * 
	 * @param nsp
	 *            Only return networks owned by this NSP, unless null.
	 * @param netType
	 *            Only return this type of network, unless null.
	 * @param location
	 *            Only return networks at this location, unless null/
	 * @return A collection of all networks matching the specified criteria.
	 */
	public Collection<AbstractNetwork> getNetworks(AbstractNetworkProvider nsp,
			Class<? extends AbstractNetwork> netType, Int2D location) {
		Collection<AbstractNetwork> networks = new ArrayList<AbstractNetwork>();

		Collection<AbstractNetworkProvider> carriers;

		if (nsp == null)
			carriers = this.networkServiceProviders;
		else {
			carriers = new ArrayList<AbstractNetworkProvider>();
			carriers.add(nsp);
		}

		for (AbstractNetworkProvider carrier : carriers) {
			Collection<AbstractNetwork> carrierNetworks;

			if (location == null)
				carrierNetworks = carrier.getEdgeNetworks();
			else
				carrierNetworks = carrier.getNetworks(location);

			if (netType == null)
				for (AbstractNetwork net : carrierNetworks)
					networks.add(net);
			else
				for (AbstractNetwork net : carrierNetworks)
					if (netType.isInstance(net))
						networks.add(net);

		}

		return networks;
	}

	public Collection<AbstractNetworkProvider> getNetworkServiceProviders() {
		return this.networkServiceProviders;
	}

	public Parameters getParameters() {
		return this.config;
	}

	/**
	 * @return The total population of All consumers at ALL locations.
	 */
	@SuppressWarnings("unchecked")
	public Double getPopulation() {
		Double pop = new Double(0);

		Iterator<AbstractConsumerClass> i = this.consumerClasses.iterator();
		while (i.hasNext()) {
			AbstractConsumerClass acc = i.next();
			pop += acc.getPopultation();
		}

		return pop;
	}

	/**
	 * @param location
	 *            The x,y coordinates on the map.
	 * @return The total population of ALL consumers at a SPECIFIC location.
	 */
	public Double getPopulation(Int2D location) {
		Double pop = new Double(0);

		Iterator<AbstractConsumerClass> i = this.consumerClasses.getObjectsAtLocation(location).iterator();
		while (i.hasNext()) {
			AbstractConsumerClass acc = i.next();
			pop += acc.getPopultation();
		}

		return pop;
	}

	/**
	 * This is only used by the user interface, and therefor efficiency is not a
	 * priority
	 * 
	 * @return A grid containing the population of each square.
	 */
	public DoubleGrid2D getPopulationGrid() {
		DoubleGrid2D ret = new DoubleGrid2D(this.config.x(), this.config.y());
		for (int i = 0; i < ret.getWidth(); i++)
			for (int j = 0; j < ret.getHeight(); j++)
				ret.set(i, j, this.getPopulation(new Int2D(i, j)));
		return ret;
	}

	/**
	 * Collects a list
	 * 
	 * @param net
	 * @param x
	 * @param y
	 * @return
	 * 
	 */
	public Map<AbstractNetworkProvider, Double> getPriceList(Class<? extends AbstractNetwork> net,
			AbstractConsumerClass acc, Int2D location) {
		Map<AbstractNetworkProvider, Double> prices = new HashMap<AbstractNetworkProvider, Double>();
		for (AbstractNetworkProvider nsp : this.getNetworkServiceProviders())
			if (nsp.hasNetworkAt(net, location))
				prices.put(nsp, nsp.getPrice(net, acc, location));

		return prices;
	}

	private void initApplicationServiceProviders() {

		// create three ASPs for each application class

		for (AppCategory ac : AppCategory.values())
			for (int i = 0; i <= 1; i++)
				this.enterMarket(new ApplicationServiceProvider(this, ac));
	}

	private void initArbiter() {
		this.schedule.scheduleRepeating(new Arbiter(), 99999999, 1);
	}

	/**
	 * Specify this somehow in parameters, rather than source.
	 */
	protected void initConsumerClasses() {
		// TODO: Clean this up.
		// Populate the landscape with a simpleconsumer class.
		for (Int2D location : this.allLocations()) {
			Double pop = this.random.nextDouble()
					* Double.parseDouble(this.config.getProperty("landscape.population.max"));
			AbstractConsumerClass acc = new ApplicationOptimizer(this, location, pop, null);
			this.enterMarket(acc);
		}

		// do it again.
		for (Int2D location : this.allLocations()) {
			Double pop = this.random.nextDouble()
					* Double.parseDouble(this.config.getProperty("landscape.population.max"));
			AbstractConsumerClass acc = new NetworkServiceMiser(this, location, pop, null);
			this.enterMarket(acc);
		}

	}

	/**
	 * DO: Specify this somehow in parameters, rather than source.
	 */
	private void initNetworkServiceProviders() {
		if (Simternet.evolve)
			this.enterMarket(new EvolvingNetworkProvider(this));
		else
			this.enterMarket(new DumbNetworkServiceProvider(this));
	}

	@Override
	public void start() {
		super.start();

		// Reset name counters, for consistency between runs in the GUI.
		this.config.resetNameCounters();

		// Initialize Consumer Agents
		this.consumerClasses = new SparseGrid2D(this.config.x(), this.config.y());
		this.initConsumerClasses();

		// Initialize Network Service Providers
		this.networkServiceProviders = new ArrayList<AbstractNetworkProvider>();
		this.initNetworkServiceProviders();

		// Initialize Application Service Providers
		this.applicationServiceProviders = new ArrayList<ApplicationServiceProvider>();
		this.ASPsByCategory = new HashMap<AppCategory, Collection<ApplicationServiceProvider>>();
		this.initApplicationServiceProviders();

		this.initArbiter();
	}

}
