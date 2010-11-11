package simternet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import simternet.application.ApplicationCategory;
import simternet.application.ApplicationServiceProvider;
import simternet.consumer.AbstractConsumerClass;
import simternet.consumer.SimpleConsumer;
import simternet.network.AbstractNetwork;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.DumbNetworkServiceProvider;
import simternet.temporal.Arbiter;

/**
 * This is the "root" class of the simulation. See the MASON documentation for
 * more details.
 * 
 * @author kkoning
 */
public class Simternet extends SimState implements Serializable {

	/**
	 * MASON includes a facility for observing and manipulating information
	 * about the simulation object itself. Sometimes the SimState object can be
	 * used directly, and Mason will allow inspection and manipulation of
	 * properties fitting the standard java conventions. (i.e., getX, setX,
	 * isX...) However, while we do want to make use of the generic user
	 * interface and charting code, we want more control over which variables
	 * are presented to the user.
	 * 
	 * This object should not store any data itself.
	 * 
	 * @author kkoning
	 */
	@SuppressWarnings("serial")
	public class SimternetInspectorObject implements Serializable {

		public Integer getDebugLevel() {
			return Simternet.this.parameters.debugLevel();
		}

		public void setDebugLevel(Integer level) {
			Simternet.this.parameters.setDebugLevel(level);
		}
	}

	/**
	 * Storing a version identifier is appropriate for this class, as we will
	 * likely be saving it often and may want to read older versions in a
	 * predictable, specified way.
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SimState.doLoop(Simternet.class, args);
		System.exit(0);
	}

	protected Collection<ApplicationCategory> appCategories = new ArrayList<ApplicationCategory>();

	/**
	 * All application service providers in the simulation
	 */
	protected Collection<ApplicationServiceProvider> applicationServiceProviders = new ArrayList<ApplicationServiceProvider>();

	/**
	 * All consumer classes in the simulation.
	 */
	protected SparseGrid2D consumerClasses;

	/**
	 * All Network Service Providers in the simulation.
	 */
	protected Collection<AbstractNetworkProvider> networkServiceProviders = new ArrayList<AbstractNetworkProvider>();

	public Exogenous parameters;
	public final SimternetInspectorObject sio = new SimternetInspectorObject();

	public Simternet(long seed) {
		this(seed, null);
	}

	public Simternet(long seed, Exogenous parameters) {
		super(seed);
		if (parameters != null)
			this.parameters = parameters;
		else
			this.parameters = Exogenous.getDefaults();

		// initialize consumer classes data structure, now that we know the size
		// of our grid.
		this.consumerClasses = new SparseGrid2D(this.parameters.x(),
				this.parameters.y());
	}

	protected void addApplicationServiceProvider(ApplicationServiceProvider asp) {
		this.applicationServiceProviders.add(asp);
		this.schedule.scheduleRepeating(asp);
	}

	/**
	 * Track this consumer class and schedule it to repeat at each step.
	 * 
	 * @param cc
	 *            The consumer class to add.
	 */
	protected void addConsumerClass(AbstractConsumerClass acc) {
		this.consumerClasses.setObjectLocation(acc, acc.getLocation());
		this.schedule.scheduleRepeating(Schedule.EPOCH, 12, acc);
	}

	/**
	 * Track this network service provider class and schedule it to repeat at
	 * each time step.
	 * 
	 * @param nsp
	 */
	protected void addNetworkServiceProvider(AbstractNetworkProvider nsp) {
		this.networkServiceProviders.add(nsp);
		this.schedule.scheduleRepeating(Schedule.EPOCH, 1, nsp);
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
				return new LocationIterator(Simternet.this.parameters.x(),
						Simternet.this.parameters.y());
			}
		};
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
		DoubleGrid2D ret = new DoubleGrid2D(this.parameters.x(),
				this.parameters.y(), initValue);

		for (AbstractNetworkProvider nsp : this.networkServiceProviders)
			for (int i = 0; i < ret.getWidth(); i++)
				for (int j = 0; j < ret.getHeight(); j++)
					ret.set(i, j, ret.get(i, j)
							+ nsp.getCustomers(new Int2D(i, j)));
		return ret;
	}

	public Collection<ApplicationServiceProvider> getApplicationServiceProviders() {
		return this.applicationServiceProviders;
	}

	public SparseGrid2D getConsumerClasses() {
		return this.consumerClasses;
	}

	public DoubleGrid2D getMyActiveSubscribersGrid(AbstractNetworkProvider np) {
		final Double initValue = 0.0;
		DoubleGrid2D ret = new DoubleGrid2D(this.parameters.x(),
				this.parameters.y(), initValue);
		for (AbstractNetworkProvider nsp : this.networkServiceProviders)
			if (this.networkServiceProviders == np)
				for (int i = 0; i < ret.getWidth(); i++)
					for (int j = 0; j < ret.getHeight(); j++) {
						ret.set(i, j, ret.get(i, j)
								+ nsp.getCustomers(new Int2D(i, j)));
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

		Iterator<AbstractConsumerClass> i = this.consumerClasses
				.getObjectsAtLocation(location).iterator();
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
		DoubleGrid2D ret = new DoubleGrid2D(this.parameters.x(),
				this.parameters.y());
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
	public Map<AbstractNetworkProvider, Double> getPriceList(
			Class<? extends AbstractNetwork> net, AbstractConsumerClass acc,
			Int2D location) {
		Map<AbstractNetworkProvider, Double> prices = new HashMap<AbstractNetworkProvider, Double>();
		for (AbstractNetworkProvider nsp : this.getNetworkServiceProviders())
			if (nsp.hasNetworkAt(net, location))
				prices.put(nsp, nsp.getPrice(net, acc, location));

		return prices;
	}

	private void initApplicationServiceProviders() {
		for (int i = 0; i < 2; i++)
			this.addApplicationServiceProvider(new ApplicationServiceProvider(
					this));
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
					* Double.parseDouble(this.parameters
							.getProperty("landscape.population.max"));
			AbstractConsumerClass acc = new SimpleConsumer(this, location, pop,
					null);
			this.addConsumerClass(acc);
		}

		// do it again.
		for (Int2D location : this.allLocations()) {
			Double pop = this.random.nextDouble()
					* Double.parseDouble(this.parameters
							.getProperty("landscape.population.max"));
			AbstractConsumerClass acc = new SimpleConsumer(this, location, pop,
					null);
			this.addConsumerClass(acc);
		}

	}

	// public void initData() {
	// this.networkServiceProviders = new HashSet<AbstractNetworkProvider>();
	// }

	/**
	 * DO: Specify this somehow in parameters, rather than source.
	 */
	private void initNetworkServiceProviders() {
		this.addNetworkServiceProvider(new DumbNetworkServiceProvider(this));
	}

	@Override
	public void start() {
		super.start();
		// this.initData();
		this.initConsumerClasses();
		this.initNetworkServiceProviders();
		this.initApplicationServiceProviders();
		this.initArbiter();
	}

}
