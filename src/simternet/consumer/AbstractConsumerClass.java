package simternet.consumer;

import java.io.Serializable;
import java.util.Map;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.NetFlow;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;
import simternet.temporal.TemporalHashMap;

@SuppressWarnings("serial")
public abstract class AbstractConsumerClass implements Steppable, AsyncUpdate,
		Serializable {

	/**
	 * The physical location of this set of consumers.
	 */
	protected final Int2D location;

	/**
	 * This is a significant data structure. It contains details on how each
	 * network is used, which includes usage of particular applications, since
	 * it must be possible to vary this based on the network the user is
	 * connected to. i.e., a user on 56k dialup is not going to watch streaming
	 * television on Hulu.
	 */
	protected final TemporalHashMap<AbstractEdgeNetwork, NetworkUsageDetails> networkUsage;

	/**
	 * The number of consumers represented by this agent.
	 */
	protected final Temporal<Double> population;

	/**
	 * Details describing the properties of this set of consumers.
	 */
	protected final ConsumerProfile profile;

	/**
	 * A link back to the simulation we are running under. Use of a singleton
	 * would be inappropriate given that Mason initializes multiple instances of
	 * a single simulation simultaneously.
	 */
	protected final Simternet s;

	/**
	 * Create a new consumer class in simulation s with location, population,
	 * and consumer profile as specified.
	 * 
	 */
	protected AbstractConsumerClass(Simternet s, Int2D location,
			Double population, ConsumerProfile profile) {
		this.s = s;
		this.location = location;
		this.profile = profile;
		this.population = new Temporal<Double>(population);
		this.networkUsage = new TemporalHashMap<AbstractEdgeNetwork, NetworkUsageDetails>();
	}

	protected void consumeApplication(ApplicationServiceProvider asp,
			ApplicationUsage usage) {
		NetFlow nf = new NetFlow();

		// total amount of usage = population * usage
		nf.amount = this.population.get();
		nf.amount *= usage.usageAmount.get();

		// usage currently directly processed by asp rather than via network
		// transversal.
		asp.processUsage(nf);
	}

	protected void consumeApplications() {

		// for each network this consumer class subscribes to
		for (Map.Entry<AbstractEdgeNetwork, NetworkUsageDetails> netMap : this.networkUsage
				.entrySet()) {

			TemporalHashMap<ApplicationServiceProvider, ApplicationUsage> appsUsage = netMap
					.getValue().getApplicationUsage();

			// for each application used on that network
			for (Map.Entry<ApplicationServiceProvider, ApplicationUsage> appUsage : appsUsage
					.entrySet())
				// Process usage individually.
				this.consumeApplication(appUsage.getKey(), appUsage.getValue());
		}
	}

	protected void consumeNetworks() {

		// for each network this consumer class subscribes to
		for (Map.Entry<AbstractEdgeNetwork, NetworkUsageDetails> netMap : this.networkUsage
				.entrySet()) {
			AbstractEdgeNetwork aen = netMap.getKey();
			NetworkUsageDetails nud = netMap.getValue();

			// Pay for our network connections.
			aen.receivePayment(this, nud.subscribers.get());
			// TODO: Is this the proper place to have the price affect utility?
		}
	}

	//
	// public boolean isSubscribed(AbstractEdgeNetwork network) {
	// return this.networkSubscriptions.equals(network);
	// }

	public Int2D getLocation() {
		return this.location;
	}

	/**
	 * If this function will be called often, it should be reduced from O=n^2 to
	 * O=1 by tracking population changes in a separate variable.
	 * 
	 * @return The TOTAL population of this specific Consumer Class at ALL
	 *         locations.
	 */
	public Double getPopultation() {
		return this.population.get();
	}

	public Double getSubscribers(AbstractEdgeNetwork aen) {
		NetworkUsageDetails nud = this.networkUsage.get(aen);
		if (nud == null)
			return 0.0;
		else
			return nud.getSubscribers();
	}

	/**
	 * Make decisions about <i>which</i> applications to use. Process their
	 * actual usage in consumeApplications().
	 */
	protected abstract void manageApplications();

	protected abstract void manageNetworks();

	// public Set<AbstractNetwork> networksSubscribed(Int2D location) {
	// HashSet<AbstractNetwork> muffin = new HashSet<AbstractNetwork>();
	// Bag bag = this.networkSubscriptions.getObjectsAtLocation(location.x,
	// location.y);
	// for (int i = 0; i < bag.numObjs; i++)
	// muffin.add((AbstractNetwork) bag.objs[i]);
	// return muffin;
	// } pop) public Double numSubscriptions(AbstractEdgeNetwork network) {
	// NetworkUsageDetails nud = this.networkUsage.get(network);
	// return nud.subscribers.get();
	// }

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
		this.networkUsage.update();
		this.population.update();
	}

}
