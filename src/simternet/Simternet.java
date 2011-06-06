package simternet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import simternet.application.AppCategory;
import simternet.application.ApplicationProvider;
import simternet.consumer.Consumer;
import simternet.consumer.DefaultConsumerProfile;
import simternet.consumer.DefinedBehaviorConsumer;
import simternet.consumer.GreedyAppManager;
import simternet.consumer.NetworkMiser;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.nsp.DumbNetworkServiceProvider;
import simternet.nsp.NetworkProvider;
import simternet.reporters.Reporter;
import simternet.temporal.Arbiter;

/**
 * This is the "root" class of the simulation. See the MASON documentation for
 * more details.
 * 
 * @author kkoning
 */
public class Simternet extends SimState implements Serializable {

	/**
	 * All application service providers in the simulation
	 */
	protected Collection<ApplicationProvider>					applicationProviders;

	protected Map<AppCategory, Collection<ApplicationProvider>>	ASPsByCategory;

	public Parameters											config;

	/**
	 * All consumer classes in the simulation.
	 */
	protected SparseGrid2D										consumerAgents;

	Collection<Steppable>										deadAgents			= new ArrayList<Steppable>();

	/**
	 * All Network Service Providers in the simulation.
	 */
	protected Collection<NetworkProvider>						networkServiceProviders;

	protected int												numConsumerAgents	= 0;
	/*
	 * Do we use an LCS to evolve the NSP?
	 */
	private static boolean										evolve				= false;

	/**
	 * Storing a version identifier is appropriate for this class, as we will
	 * likely be saving it often and may want to read older versions in a
	 * predictable, specified way.
	 * 
	 */
	private static final long									serialVersionUID	= 1L;

	public static void main(String[] args) {
		for (String s : args)
			if (s.equals("--evolve"))
				Simternet.evolve = true;
		SimState.doLoop(Simternet.class, args);
		System.exit(0);
	}

	public Simternet(long seed) {
		this(seed, null);
	}

	public Simternet(long seed, boolean evolve) {
		this(seed, null, evolve);
	}

	public Simternet(long seed, Parameters config) {
		this(seed, config, Simternet.evolve);
	}

	public Simternet(long seed, Parameters config, boolean evolve) {
		super(seed);

		Simternet.evolve = evolve;

		if (config != null)
			this.config = config;
		else
			this.config = new Parameters();

	}

	public void addReporter(Reporter r) {
		this.schedule.scheduleRepeating(Schedule.EPOCH, 100, r);
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
	 * This function should be called whenever an agent becomes active. It
	 * should take care of adding it to all the appropriate tracking data
	 * structures and the MASON schedule.
	 * 
	 * @param agent
	 */
	public void enterMarket(Steppable agent) {

		int ordering = 0; // determines execution priority of agents, lowest #s
		// first.

		if (agent instanceof Consumer) {
			Consumer acc = (Consumer) agent;
			this.consumerAgents.setObjectLocation(acc, acc.getLocation());
			ordering = 3;
			this.numConsumerAgents++;
		} else if (agent instanceof ApplicationProvider) {
			ApplicationProvider asp = (ApplicationProvider) agent;
			this.applicationProviders.add(asp);
			Collection<ApplicationProvider> appsInCategory = this.ASPsByCategory.get(asp.getAppCategory());
			if (appsInCategory == null) {
				appsInCategory = new ArrayList<ApplicationProvider>();
				this.ASPsByCategory.put(asp.getAppCategory(), appsInCategory);
			}
			appsInCategory.add(asp);
			ordering = 2;
		} else if (agent instanceof NetworkProvider) {
			ordering = 1;
			NetworkProvider nsp = (NetworkProvider) agent;
			this.networkServiceProviders.add(nsp);
		} else
			throw new RuntimeException("Adding unknown agent");

		this.schedule.scheduleRepeating(Schedule.EPOCH, ordering, agent);
		if (TraceConfig.marketEntry && Logger.getRootLogger().isTraceEnabled())
			Logger.getRootLogger().log(Level.INFO, "Market Entry: " + agent);
	}

	@Override
	public void finish() {
		super.finish();

		List<Network> nets = new ArrayList<Network>();

		for (ApplicationProvider asp : this.applicationProviders)
			nets.add(asp.getDatacenter());

		for (NetworkProvider nsp : this.networkServiceProviders) {
			nets.add(nsp.getBackboneNetwork());
			for (Network aen : nsp.getEdgeNetworks())
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

		for (NetworkProvider nsp : this.networkServiceProviders)
			for (int i = 0; i < ret.getWidth(); i++)
				for (int j = 0; j < ret.getHeight(); j++)
					ret.set(i, j, ret.get(i, j) + nsp.getCustomers(new Int2D(i, j)));
		return ret;
	}

	public Collection<ApplicationProvider> getASPs() {
		return this.applicationProviders;
	}

	public Collection<ApplicationProvider> getASPs(AppCategory c) {
		Collection<ApplicationProvider> asps = this.ASPsByCategory.get(c);
		if (asps == null) {
			asps = new ArrayList<ApplicationProvider>();
			this.ASPsByCategory.put(c, asps);
		}
		return asps;
	}

	public SparseGrid2D getConsumerClasses() {
		return this.consumerAgents;
	}

	public DoubleGrid2D getMyActiveSubscribersGrid(NetworkProvider np) {
		final Double initValue = 0.0;
		DoubleGrid2D ret = new DoubleGrid2D(this.config.x(), this.config.y(), initValue);
		for (NetworkProvider nsp : this.networkServiceProviders)
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
	 *            Only return networks at this location, unless null
	 * @return A collection of all networks matching the specified criteria.
	 */
	public Collection<Network> getNetworks(NetworkProvider nsp, Class<? extends Network> netType, Int2D location) {
		Collection<Network> networks = new ArrayList<Network>();

		Collection<NetworkProvider> carriers;

		if (nsp == null)
			carriers = this.networkServiceProviders;
		else {
			carriers = new ArrayList<NetworkProvider>();
			carriers.add(nsp);
		}

		for (NetworkProvider carrier : carriers) {
			Collection<Network> carrierNetworks;

			if (location == null)
				carrierNetworks = carrier.getNetworks();
			else
				carrierNetworks = carrier.getNetworks(location);

			if (netType == null)
				for (Network net : carrierNetworks)
					networks.add(net);
			else
				for (Network net : carrierNetworks)
					if (netType.isInstance(net))
						networks.add(net);

		}

		return networks;
	}

	public Collection<NetworkProvider> getNetworkServiceProviders() {
		return this.networkServiceProviders;
	}

	public int getNumConsumerAgents() {
		return this.numConsumerAgents;
	}

	public Double getNumNetworkProviders(Int2D location) {
		Double providersWithNetworks = 0.0;
		for (NetworkProvider nsp : this.networkServiceProviders)
			if (nsp.hasNetworkAt(EdgeNetwork.class, location))
				providersWithNetworks++;
		return providersWithNetworks;
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

		Iterator<Consumer> i = this.consumerAgents.iterator();
		while (i.hasNext()) {
			Consumer acc = i.next();
			pop += acc.getPopulation();
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

		Iterator<Consumer> i = this.consumerAgents.getObjectsAtLocation(location).iterator();
		while (i.hasNext()) {
			Consumer acc = i.next();
			pop += acc.getPopulation();
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
	// public Map<AbstractNetworkProvider, Double> getPriceList(Class<? extends
	// AbstractNetwork> net,
	// AbstractConsumerClass acc, Int2D location) {
	// Map<AbstractNetworkProvider, Double> prices = new
	// HashMap<AbstractNetworkProvider, Double>();
	// for (AbstractNetworkProvider nsp : this.getNetworkServiceProviders())
	// if (nsp.hasNetworkAt(net, location))
	// prices.put(nsp, nsp.getPrice(net, acc, location));
	//
	// return prices;
	// }

	private void initApplicationServiceProviders() {

		// create three ASPs for each application class

		for (AppCategory ac : AppCategory.values())
			for (int i = 0; i <= 1; i++)
				this.enterMarket(new ApplicationProvider(this, ac));
	}

	private void initArbiter() {
		this.schedule.scheduleRepeating(new Arbiter(), 99999999, 1);
	}

	/**
	 * Specify this somehow in parameters, rather than source.
	 */
	protected void initConsumerClasses() {

		DefaultConsumerProfile defaultProfile = new DefaultConsumerProfile();
		for (int i = 0; i < 40; i++)
			// TODO: Clean this up.
			// Populate the landscape with a simpleconsumer class.
			for (Int2D location : this.allLocations()) {
				Double pop = this.random.nextDouble()
						* Double.parseDouble(this.config.getProperty("landscape.population.max"));
				Consumer acc = new Consumer(this, location, pop, defaultProfile, NetworkMiser.getSingleton(),
						GreedyAppManager.getSingleton(), null);
				this.enterMarket(acc);
			}

		// Add some consumers with defined behavior
		for (Int2D location : this.allLocations()) {
			Double pop = this.random.nextDouble()
					* Double.parseDouble(this.config.getProperty("landscape.population.max"));
			Consumer acc = new DefinedBehaviorConsumer(this, location, pop, defaultProfile);
			this.enterMarket(acc);
		}

	}

	/**
	 * DO: Specify this somehow in parameters, rather than source.
	 */
	private void initNetworkServiceProviders() {
		if (Simternet.evolve)
			// this.enterMarket(new EvolvingNetworkProvider(this));
			;
		else
			this.enterMarket(new DumbNetworkServiceProvider(this));
	}

	@Override
	public void start() {
		super.start();

		// Reset name counters, for consistency between runs in the GUI.
		this.config.resetNameCounters();

		// Initialize Consumer Agents
		this.consumerAgents = new SparseGrid2D(this.config.x(), this.config.y());
		this.initConsumerClasses();

		// Initialize Network Service Providers
		this.networkServiceProviders = new ArrayList<NetworkProvider>();
		this.initNetworkServiceProviders();

		// Initialize Application Service Providers
		this.applicationProviders = new ArrayList<ApplicationProvider>();
		this.ASPsByCategory = new HashMap<AppCategory, Collection<ApplicationProvider>>();
		this.initApplicationServiceProviders();

		// Reporter edr = new EdgeDataReporter();
		// edr.setGeneration(0);
		// edr.setChunk(0);
		// this.addReporter(edr);
		// Reporter nfr = new NetworkProviderFitnessReporter();
		// nfr.setGeneration(0);
		// nfr.setChunk(0);
		// this.addReporter(nfr);

		this.initArbiter();
	}
}
