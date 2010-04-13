package simternet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import simternet.consumer.AbstractConsumerClass;
import simternet.consumer.IndifferentLazyConsumer;
import simternet.network.AbstractNetwork;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.RepeatedStackelburgNSP;
import simternet.temporal.Arbiter;

/**
 * @author kkoning
 * 
 *         This is the "root" class of the simulation. See the MASON
 *         documentation for more details.
 * 
 * 
 */
public class Simternet extends SimState {

	/**
	 * Constant used in Cournot competition
	 */
	public final static Double ALPHA = 100.0;

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

	/**
	 * Stores a list of ALL consumer classes present in the simulation.
	 */
	protected Set<AbstractConsumerClass> consumerClasses = new HashSet<AbstractConsumerClass>();

	/**
	 * Stores a list of ALL network service providers present in the simulation.
	 */
	protected Set<AbstractNetworkProvider> networkServiceProviders = new HashSet<AbstractNetworkProvider>();

	public Simternet(long seed) {
		super(seed);
	}

	/**
	 * @param cc
	 * 
	 *            Track this consumer class and schedule it to repeat at each
	 *            step. Ordering steps.
	 * 
	 */
	protected void addConsumerClass(AbstractConsumerClass cc) {
		this.consumerClasses.add(cc);
		this.schedule.scheduleRepeating(Schedule.EPOCH, 12, cc);
		// schedule.scheduleRepeating(cc);
	}

	/**
	 * Track this consumer class and schedule it to repeat at each step.
	 * 
	 * @param cc
	 *            Consumer class to add to the schedule
	 * @param ordering
	 *            The order (> 0) in which the item should run
	 * @param interval
	 *            The interval (> 0) after which the object should be run
	 * 
	 */
	protected void addConsumerClass(AbstractConsumerClass cc, int ordering,
			double interval) {
		this.consumerClasses.add(cc);
		this.schedule.scheduleRepeating(cc, ordering, interval);
	}

	/**
	 * @param nsp
	 * 
	 *            Track this network service provider class and schedule it to
	 *            repeat at each time step.
	 * 
	 */
	protected void addNetworkServiceProvider(AbstractNetworkProvider nsp) {
		this.networkServiceProviders.add(nsp);
		this.schedule.scheduleRepeating(Schedule.EPOCH, 1, nsp);
		// schedule.scheduleRepeating(nsp);
	}

	/**
	 * Track this Network Service Provider class and schedule it to repeat at
	 * each step.
	 * 
	 * @param nsp
	 *            Network Service Provider class to add to the schedule
	 * @param ordering
	 *            The order (> 0) in which the item should run
	 * @param interval
	 *            The interval (> 0) after which the object should be run
	 * 
	 */
	protected void addNetworkServiceProvider(AbstractNetworkProvider nsp,
			int ordering, double interval) {
		this.networkServiceProviders.add(nsp);
		this.schedule.scheduleRepeating(nsp, ordering, interval);
	}

	public Set<AbstractConsumerClass> getConsumerClasses() {
		return this.consumerClasses;
	}

	public Set<AbstractNetworkProvider> getNetworkServiceProviders() {
		return this.networkServiceProviders;
	}

	public Integer getNumNetworks(Class<? extends AbstractNetwork> net,
			Integer x, Integer y) {
		Integer num = 0;
		for (AbstractNetworkProvider nsp : this.networkServiceProviders)
			if (nsp.hasNetworkAt(net, x, y))
				num++;
		return num;
	}

	/**
	 * @return The total population of All consumers at ALL locations.
	 */
	public Double getPopulation() {
		Double pop = new Double(0);
		for (AbstractConsumerClass cc : this.consumerClasses)
			pop += cc.getPopultation();
		return pop;
	}

	/**
	 * @param x
	 * @param y
	 * @return The total population of ALL consumers at a SPECIFIC location.
	 */
	public Double getPopulation(Integer x, Integer y) {
		Double pop = new Double(0);
		for (AbstractConsumerClass cc : this.consumerClasses)
			pop += cc.getPopulation(x, y);
		return pop;
	}

	public DoubleGrid2D getPopulationGrid() {
		DoubleGrid2D ret = new DoubleGrid2D(Exogenous.landscapeX,
				Exogenous.landscapeY);
		for (int i = 0; i < ret.getWidth(); i++)
			for (int j = 0; j < ret.getHeight(); j++)
				ret.set(i, j, this.getPopulation(i, j));
		return ret;
	}

	/**
	 * @param net
	 * @param x
	 * @param y
	 * @return
	 * 
	 *         Collects a list
	 * 
	 */
	public Map<AbstractNetworkProvider, Double> getPriceList(
			Class<? extends AbstractNetwork> net, AbstractConsumerClass acc,
			Integer x, Integer y) {
		Map<AbstractNetworkProvider, Double> prices = new HashMap<AbstractNetworkProvider, Double>();
		for (AbstractNetworkProvider nsp : this.getNetworkServiceProviders())
			if (nsp.hasNetworkAt(net, x, y))
				prices.put(nsp, nsp.getPrice(net, acc, x, y));

		return prices;
	}

	private void initArbiter() {
		this.schedule.scheduleRepeating(new Arbiter(), 99999999, 1);
	}

	/**
	 * Specify this somehow in parameters, rather than source.
	 */
	protected void initConsumerClasses() {
		// addConsumerClass(new SimpleConsumer(this));
		this.addConsumerClass(new IndifferentLazyConsumer(this));
	}

	/**
	 * DO: Specify this somehow in parameters, rather than source.
	 */
	private void initNetworkServiceProviders() {
		// addNetworkServiceProvider(new DumbNetworkServiceProvider(this));
		// addNetworkServiceProvider(new DumbNetworkServiceProvider(this));
		this.addNetworkServiceProvider(new RepeatedStackelburgNSP(this));
		this.addNetworkServiceProvider(new RepeatedStackelburgNSP(this));
		this.addNetworkServiceProvider(new RepeatedStackelburgNSP(this));
	}

	@Override
	public void start() {
		super.start();
		this.initConsumerClasses();
		this.initNetworkServiceProviders();
		this.initArbiter();
	}

}
