package simternet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import sim.engine.Schedule;
import sim.engine.SimState;
import simternet.consumer.AbstractConsumerClass;
import simternet.consumer.IndifferentLazyConsumer;
import simternet.consumer.SimpleConsumer;
import simternet.network.AbstractNetwork;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.DumbNetworkServiceProvider;
import simternet.nsp.RepeatedStackelburgNSP;

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
	 * Storing a version identifier is appropriate for this class, as we will
	 * likely be saving it often and may want to read older versions in a
	 * predictable, specified way.
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public static void main(String[] args) {
		doLoop(Simternet.class, args);
        System.exit(0);
	}
	
	public Set<AbstractConsumerClass> getConsumerClasses() {
		return consumerClasses;
	}

	public void setConsumerClasses(Set<AbstractConsumerClass> consumerClasses) {
		this.consumerClasses = consumerClasses;
	}

	public Set<AbstractNetworkProvider> getNetworkServiceProviders() {
		return networkServiceProviders;
	}

	public void setNetworkServiceProviders(Set<AbstractNetworkProvider> nspClasses) {
		this.networkServiceProviders = nspClasses;
	}

	/**
	 * @param cc
	 * 
	 * Track this consumer class and schedule it to repeat at each step.
	 *   Ordering steps.
	 * 
	 */
	protected void addConsumerClass(AbstractConsumerClass cc) {
		consumerClasses.add(cc);
		schedule.scheduleRepeating(Schedule.EPOCH, 12, cc);
//		schedule.scheduleRepeating(cc);
	}
	
	/**
	 * @param nsp
	 * 
	 * Track this network service provider class and schedule it to
	 * repeat at each time step.
	 * 
	 */
	protected void addNetworkServiceProvider(AbstractNetworkProvider nsp) {
		networkServiceProviders.add(nsp);
		schedule.scheduleRepeating(Schedule.EPOCH, 1, nsp);
//		schedule.scheduleRepeating(nsp);
	}
	
	/**
	 * @return The total population of All consumers at ALL locations.
	 */
	public Double getPopulation() {
		Double pop = new Double(0);
		for (AbstractConsumerClass cc : consumerClasses)
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
		for (AbstractConsumerClass cc : consumerClasses)
			pop += cc.getPopulation(x, y);
		return pop;
	}
	
	/**
	 *   Specify this somehow in parameters, rather than source.
	 */
	protected void initConsumerClasses() {
//		AbstractConsumerClass cc = new SimpleConsumer(this);
//		addConsumerClass(cc);
		addConsumerClass(new IndifferentLazyConsumer(this));
	}
	
	/**
	 * DO:  Specify this somehow in parameters, rather than source.
	 */
	private void initNetworkServiceProviders() {
//		AbstractNetworkProvider nsp = new DumbNetworkServiceProvider(this);
//		addNetworkServiceProvider(nsp);
		addNetworkServiceProvider(new RepeatedStackelburgNSP(this));
		addNetworkServiceProvider(new RepeatedStackelburgNSP(this));
		addNetworkServiceProvider(new RepeatedStackelburgNSP(this));
	}
	
	public void start() {
		super.start();
		initConsumerClasses();
		initNetworkServiceProviders();
	}
	
	/**
	 * @param net
	 * @param x
	 * @param y
	 * @return
	 * 
	 * Collects a list
	 * 
	 */
	public Map<AbstractNetworkProvider, Double> getPriceList(
			Class<? extends AbstractNetwork> net, AbstractConsumerClass acc,
			Integer x, Integer y) {
		Map<AbstractNetworkProvider, Double> prices = new HashMap<AbstractNetworkProvider, Double>();
		for (AbstractNetworkProvider nsp : this.getNetworkServiceProviders()) {
			if (nsp.hasNetworkAt(net, x, y))
				prices.put(nsp, nsp.getPrice(net, acc, x, y));
		}

		return prices;
	}
	

}
