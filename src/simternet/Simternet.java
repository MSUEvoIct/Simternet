package simternet;

import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;

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
	protected List<AbstractConsumer> consumerClasses = new ArrayList<AbstractConsumer>();
	/**
	 * Stores a list of ALL network service providers present in the simulation.
	 */
	protected List<AbstractNetworkProvider> nspClasses = new ArrayList<AbstractNetworkProvider>();

	
	public Simternet(long seed) {
		super(seed);
	}

	public static void main(String[] args) {
		doLoop(Simternet.class, args);
        System.exit(0);
	}
	
	public List<AbstractConsumer> getConsumerClasses() {
		return consumerClasses;
	}

	public void setConsumerClasses(List<AbstractConsumer> consumerClasses) {
		this.consumerClasses = consumerClasses;
	}

	public List<AbstractNetworkProvider> getNspClasses() {
		return nspClasses;
	}

	public void setNspClasses(List<AbstractNetworkProvider> nspClasses) {
		this.nspClasses = nspClasses;
	}

	/**
	 * @param cc
	 * 
	 * Track this consumer class and schedule it to repeat at each step.
	 *   Ordering steps.
	 * 
	 */
	private void addConsumerClass(AbstractConsumer cc) {
		consumerClasses.add(cc);
		schedule.scheduleRepeating(cc);
	}
	
	/**
	 * @param nsp
	 * 
	 * Track this network service provider class and schedule it to
	 * repeat at each time step.
	 * 
	 */
	private void addNetworkServiceProvider(AbstractNetworkProvider nsp) {
		nspClasses.add(nsp);
		schedule.scheduleRepeating(nsp);
	}
	
	/**
	 * @return The total population of All consumers at ALL locations.
	 */
	public Double getPopulation() {
		Double pop = new Double(0);
		for (AbstractConsumer cc : consumerClasses)
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
		for (AbstractConsumer cc : consumerClasses)
			pop += cc.getPopulation(x, y);
		return pop;
	}
	
	/**
	 *   Specify this somehow in parameters, rather than source.
	 */
	private void initConsumerClasses() {
		AbstractConsumer cc = new SimpleConsumer(this);
		addConsumerClass(cc);
	}
	
	/**
	 * DO:  Specify this somehow in parameters, rather than source.
	 */
	private void initNetworkServiceProviders() {
		AbstractNetworkProvider nsp = new DumbNetworkServiceProvider(this);
		addNetworkServiceProvider(nsp);
	}
	
	public void start() {
		super.start();
		initConsumerClasses();
		initNetworkServiceProviders();
	}
	

}
