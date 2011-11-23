package simternet.engine;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import simternet.agents.MarketInfo;
import simternet.agents.asp.AppCategory;
import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.Consumer;
import simternet.agents.consumer.behavior.DefaultAppBenefitCalculator;
import simternet.agents.consumer.behavior.DefaultAppCategoryBudgetCalculator;
import simternet.agents.consumer.behavior.GreedyAppManager;
import simternet.agents.consumer.behavior.RationalNetManager;
import simternet.agents.nsp.NetworkProvider;
import simternet.data.output.ASPInterconnectionReporter;
import simternet.data.output.AggregateConsumerDataReporter;
import simternet.data.output.ApplicationProviderFitnessReporter;
import simternet.data.output.BackboneLinkReporter;
import simternet.data.output.ECJEvolutionReporterComponent;
import simternet.data.output.EdgeDataReporter;
import simternet.data.output.EdgeMarketReporter;
import simternet.data.output.NetworkProviderFitnessReporter;
import simternet.data.output.Reporter;
import simternet.data.output.StepReporterComponent;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * This is the "root" class of the simulation. See the MASON documentation for
 * more details.
 * 
 * @author kkoning
 */
public class Simternet extends SimState implements Serializable {
	private static final long									serialVersionUID	= 1L;

	/*
	 * Data structures to keep track of agents
	 */
	protected Collection<ApplicationProvider>					applicationProviders;
	protected Map<AppCategory, Collection<ApplicationProvider>>	applicationProvidersByCategory;
	protected SparseGrid2D										consumerAgents;
	protected Collection<Steppable>								deadAgents			= new ArrayList<Steppable>();
	protected Collection<NetworkProvider>						networkServiceProviders;
	public int													numASPs				= 0;
	public int													numConsumerAgents	= 0;
	public int													numNSPs				= 0;

	/*
	 * Tracking Variables for ECJ
	 */
	public int													generation;
	public int													chunk;

	/*
	 * Exogenous model parameters
	 */
	public SimternetConfig										config				= new SimternetConfig(this);

	/**
	 * 
	 */
	public MarketInfo											marketInfo			= new MarketInfo(this);

	public static final DecimalFormat							nf					= new DecimalFormat("0.###E0");

	/**
	 * @param seed
	 *            Random seed for this Simternet
	 */
	public Simternet(long seed) {
		super(seed);
		schedule = new AsyncDataSchedule();

	}

	@Override
	public void start() {
		super.start();

		initReporters();

		// Initialize Network Service Providers
		networkServiceProviders = new ArrayList<NetworkProvider>();

		// Initialize Application Service Providers
		applicationProviders = new ArrayList<ApplicationProvider>();
		applicationProvidersByCategory = new HashMap<AppCategory, Collection<ApplicationProvider>>();

		// Initialize Consumer Agents
		consumerAgents = new SparseGrid2D(config.gridSize.x, config.gridSize.y);
		initConsumerClasses();
		// Do not insert NSP or ASP agents ourselves; let ECJ handle that.

	}

	public void addReporter(Reporter r) {
		AsyncDataSchedule s2 = (AsyncDataSchedule) schedule;
		s2.addReporter(r);
	}

	/**
	 * A convenience method providing an iterator for all locations.
	 * 
	 * @return An iterator of all locations as Int2D objects.
	 */
	public Iterable<Int2D> allLocations() {
		return new Iterable<Int2D>() {
			@Override
			public Iterator<Int2D> iterator() {
				return new LocationIterator(config.gridSize.x, config.gridSize.y);
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
			acc.setName(config.prefixConsumer + numConsumerAgents++);

			consumerAgents.setObjectLocation(acc, acc.getLocation());
			ordering = 3;
		} else if (agent instanceof ApplicationProvider) {
			ApplicationProvider asp = (ApplicationProvider) agent;
			asp.setName(config.prefixASP + numASPs++);

			applicationProviders.add(asp);
			Collection<ApplicationProvider> appsInCategory = applicationProvidersByCategory.get(asp.getAppCategory());
			if (appsInCategory == null) {
				appsInCategory = new ArrayList<ApplicationProvider>();
				applicationProvidersByCategory.put(asp.getAppCategory(), appsInCategory);
			}
			appsInCategory.add(asp);
			ordering = 2;
		} else if (agent instanceof NetworkProvider) {
			NetworkProvider nsp = (NetworkProvider) agent;
			nsp.setName(config.prefixNSP + numNSPs++);

			ordering = 1;
			networkServiceProviders.add(nsp);
		} else
			throw new RuntimeException("Adding unknown agent");

		schedule.scheduleRepeating(Schedule.EPOCH, ordering, agent);
		if (TraceConfig.finance.marketEntry) {
			TraceConfig.out.println("Market Entry: " + agent);
		}
	}

	@Override
	public void finish() {
		super.finish();
		Collection<Reporter> reporters = ((AsyncDataSchedule) schedule).reporters;
		for (Reporter reporter : reporters) {
			reporter.finish();
		}

		if (TraceConfig.out != null) {
			TraceConfig.out.close();
		}

		// TODO: Why is this here? isn't finish() called just before Simternet
		// is garbage collected? Ans: This was to output a network connection
		// graph.
		// List<Network> nets = new ArrayList<Network>();
		//
		// for (ApplicationProvider asp : applicationProviders) {
		// nets.add(asp.getDatacenter());
		// }
		//
		// for (NetworkProvider nsp : networkServiceProviders) {
		// nets.add(nsp.getBackboneNetwork());
		// for (Network aen : nsp.getEdgeNetworks()) {
		// nets.add(aen);
		// }
		// }
		//
		// NetworkGraphDataOutput ngdo = new NetworkGraphDataOutput(this, nets);
		//
		// ngdo.output();

	}

	/**
	 * @return A grid containing the number of active subscribers in each
	 *         square. This result is calculated, and so any modification is of
	 *         course ineffective.
	 */
	public DoubleGrid2D getAllActiveSubscribersGrid() {
		final Double initValue = 0.0;
		DoubleGrid2D ret = new DoubleGrid2D(config.gridSize.x, config.gridSize.y, initValue);

		for (NetworkProvider nsp : networkServiceProviders) {
			for (int i = 0; i < ret.getWidth(); i++) {
				for (int j = 0; j < ret.getHeight(); j++) {
					ret.set(i, j, ret.get(i, j) + nsp.getCustomers(new Int2D(i, j)));
				}
			}
		}
		return ret;
	}

	public Collection<ApplicationProvider> getASPs() {
		return applicationProviders;
	}

	public ApplicationProvider getASP(String name) {
		for (ApplicationProvider asp : applicationProviders) {
			if (asp.getName().equals(name))
				return asp;
		}
		return null;
	}

	public NetworkProvider getNSP(String name) {
		for (NetworkProvider nsp : networkServiceProviders) {
			if (nsp.getName().equals(name))
				return nsp;
		}
		return null;
	}

	public Collection<ApplicationProvider> getASPs(AppCategory c) {
		Collection<ApplicationProvider> asps = applicationProvidersByCategory.get(c);
		if (asps == null) {
			asps = new ArrayList<ApplicationProvider>();
			applicationProvidersByCategory.put(c, asps);
		}
		return asps;
	}

	public SparseGrid2D getConsumerClasses() {
		return consumerAgents;
	}

	public DoubleGrid2D getMyActiveSubscribersGrid(NetworkProvider np) {
		final Double initValue = 0.0;
		DoubleGrid2D ret = new DoubleGrid2D(config.gridSize.x, config.gridSize.y, initValue);
		for (NetworkProvider nsp : networkServiceProviders)
			if (networkServiceProviders == np) {
				for (int i = 0; i < ret.getWidth(); i++) {
					for (int j = 0; j < ret.getHeight(); j++) {
						ret.set(i, j, ret.get(i, j) + nsp.getCustomers(new Int2D(i, j)));
						System.out.println(ret.get(i, j));
					}
				}
			}
		return ret;
	}

	public Collection<Network> getNetworks(Int2D location) {
		return this.getNetworks(null, null, location);
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

		if (nsp == null) {
			carriers = networkServiceProviders;
		} else {
			carriers = new ArrayList<NetworkProvider>();
			carriers.add(nsp);
		}

		for (NetworkProvider carrier : carriers) {
			Collection<Network> carrierNetworks;

			if (location == null) {
				carrierNetworks = carrier.getNetworks();
			} else {
				carrierNetworks = carrier.getNetworks(location);
			}

			if (netType == null) {
				for (Network net : carrierNetworks) {
					networks.add(net);
				}
			} else {
				for (Network net : carrierNetworks)
					if (netType.isInstance(net)) {
						networks.add(net);
					}
			}

		}

		return networks;
	}

	public Collection<NetworkProvider> getNetworkServiceProviders() {
		return networkServiceProviders;
	}

	public int getNumConsumerAgents() {
		return numConsumerAgents;
	}

	public Integer getNumNetworkProviders(Int2D location) {
		Integer providersWithNetworks = 0;
		for (NetworkProvider nsp : networkServiceProviders)
			if (nsp.hasNetworkAt(EdgeNetwork.class, location)) {
				providersWithNetworks++;
			}
		return providersWithNetworks;
	}

	// public Parameters getParameters() {
	// return this.config;
	// }

	/**
	 * @return The total population of All consumers at ALL locations.
	 */
	@SuppressWarnings("unchecked")
	public Double getPopulation() {
		Double pop = new Double(0);

		Iterator<Consumer> i = consumerAgents.iterator();
		while (i.hasNext()) {
			Consumer acc = i.next();
			pop += acc.getPopulation();
		}

		return pop;
	}

	/**
	 * @return The number of Consumers using any NSP
	 */
	public Double getNumActiveNSPUsers() {
		double numUsers = 0.0;
		for (Int2D location : allLocations()) {
			numUsers += getNumActiveNSPUsers(location);
		}
		return numUsers;
	}

	public Double getNumActiveNSPUsers(Int2D location) {
		Double numUsers = 0.0;
		Bag consumers = consumerAgents.getObjectsAtLocation(location);
		if (consumers == null)
			return 0.0;
		else if (consumers.isEmpty())
			return 0.0;
		else {
			for (int i = 0; i < consumers.numObjs; i++) {
				Consumer c = (Consumer) consumers.objs[i];
				if (c.getEdgeNetwork().get() != null) {
					numUsers += c.getPopulation();
				}
			}
		}
		return numUsers;
	}

	/**
	 * @param location
	 *            The x,y coordinates on the map.
	 * @return The total population of ALL consumers at a SPECIFIC location.
	 */
	public Double getPopulation(Int2D location) {
		Double pop = new Double(0);

		Bag consumers = consumerAgents.getObjectsAtLocation(location);
		if (consumers == null)
			return 0.0;

		Iterator<Consumer> i = consumerAgents.getObjectsAtLocation(location).iterator();
		while (i.hasNext()) {
			Consumer acc = i.next();
			pop += acc.getPopulation();
		}

		return pop;
	}

	/**
	 * This is only used by the user interface, and therefore efficiency is not
	 * a priority
	 * 
	 * @return A grid containing the population of each square.
	 */
	public DoubleGrid2D getPopulationGrid() {
		DoubleGrid2D ret = new DoubleGrid2D(config.gridSize.x, config.gridSize.y);
		for (int i = 0; i < ret.getWidth(); i++) {
			for (int j = 0; j < ret.getHeight(); j++) {
				ret.set(i, j, this.getPopulation(new Int2D(i, j)));
			}
		}
		return ret;
	}

	/**
	 * Adds 40 consumer agents at each grid square.
	 * 
	 * TODO: Parameterize?
	 */
	protected void initConsumerClasses() {

		for (int i = 0; i < 40; i++) {
			// TODO: Clean this up.
			// Populate the landscape with a simpleconsumer class.
			for (Int2D location : allLocations()) {
				Double pop = random.nextDouble() * config.consumerPopulationMax;
				// Consumer acc = new Consumer(this, location, pop,
				// defaultProfile, NetworkMiser.getSingleton(),
				// GreedyAppManager.getSingleton(), null);
				Consumer c = new Consumer(this, location, pop, RationalNetManager.getSingleton(),
						GreedyAppManager.getSingleton(), DefaultAppBenefitCalculator.getSingleton(),
						DefaultAppCategoryBudgetCalculator.getSingleton());
				// Consumer c = new Consumer(this, location, pop,
				// UtilityNetManager.getSingleton(),
				// GreedyAppManager.getSingleton(),
				// DefaultAppBenefitCalculator.getSingleton(),
				// DefaultAppCategoryBudgetCalculator.getSingleton());

				enterMarket(c);
			}
		}
	}

	private void initReporters() {

		int genModulo = config.reporterGenerationModulo;
		int chunkModulo = config.reporterChunkModulo;
		int stepModulo = config.reporterStepModulo;

		// Don't bother initializing these reporters if we're never going to run
		// them anyway
		if (generation % genModulo != 0)
			return;

		if (chunk % chunkModulo != 0)
			return;

		ECJEvolutionReporterComponent eerc = new ECJEvolutionReporterComponent(this);
		StepReporterComponent src = new StepReporterComponent(this);

		// TODO: Parmeterize which reporters are activated
		ApplicationProviderFitnessReporter apfr = new ApplicationProviderFitnessReporter(this);
		apfr.addComponent(eerc);
		apfr.addComponent(src);
		apfr.stepModulo = stepModulo;
		addReporter(apfr);

		// Network Provider Fitness Reporter
		NetworkProviderFitnessReporter npfr = new NetworkProviderFitnessReporter(this);
		npfr.addComponent(eerc);
		npfr.addComponent(src);
		npfr.stepModulo = stepModulo;
		addReporter(npfr);

		// Consumer Data Reporter
		// Reporter cdr2 = new ConsumerDataReporter(this);
		// cdr2.addComponent(eerc);
		// cdr2.addComponent(src);
		// cdr2.stepModulo = stepModulo;
		// addReporter(cdr2);

		// Aggregate Consumer Data Reporter
		AggregateConsumerDataReporter acdr = new AggregateConsumerDataReporter(this);
		acdr.addComponent(eerc);
		acdr.addComponent(src);
		acdr.stepModulo = stepModulo;
		addReporter(acdr);

		// Backbone Link Reporter
		BackboneLinkReporter blr = new BackboneLinkReporter(this);
		blr.addComponent(eerc);
		blr.addComponent(src);
		blr.stepModulo = stepModulo;
		addReporter(blr);

		// ASP Interconnection Reporter
		ASPInterconnectionReporter air = new ASPInterconnectionReporter(this);
		air.addComponent(eerc);
		air.addComponent(src);
		air.stepModulo = stepModulo;
		addReporter(air);

		// Edge Market Reporter
		EdgeMarketReporter emr = new EdgeMarketReporter(this);
		emr.addComponent(eerc);
		emr.addComponent(src);
		emr.stepModulo = stepModulo;
		addReporter(emr);

		// Edge Data Reporter
		EdgeDataReporter edr = new EdgeDataReporter(this);
		edr.addComponent(eerc);
		edr.addComponent(src);
		edr.stepModulo = stepModulo;
		addReporter(edr);
	}
}
