package simternet.consumer;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.AppCategory;
import simternet.application.ApplicationServiceProvider;
import simternet.network.EdgeNetwork;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;
import simternet.temporal.TemporalHashMap;

@SuppressWarnings("serial")
public abstract class AbstractConsumerClass implements Steppable, AsyncUpdate, Serializable {

	/**
	 * Consumers do not consume all applications. Whether the budget constraint
	 * is measured in time or dollars, consumers will not spend more than this
	 * amount on the app class during the next period. To the extent that those
	 * preferences change over time (i.e., consumers tending to spend more time
	 * consuming entertainment content), this should be reflected by changing
	 * these values.
	 */
	protected TemporalHashMap<AppCategory, Double>								appBudgetConstraints	= new TemporalHashMap<AppCategory, Double>();

	/**
	 * The actual list of application service providers selected for use, sorted
	 * by category. manageApplications() is responsible for maintaining this
	 * data structure, based on appBudgetConstraints and the qualities of the
	 * applications themselves.
	 */
	protected TemporalHashMap<AppCategory, List<ApplicationServiceProvider>>	appsUsed				= new TemporalHashMap<AppCategory, List<ApplicationServiceProvider>>();

	/**
	 * The physical location of this set of consumers.
	 */
	protected final Int2D														location;

	/**
	 * A human-readable name to distinguish this consumer agent
	 */
	protected final String														name;

	/**
	 * The number of consumers represented by this agent.
	 */
	protected final Temporal<Double>											population;

	/**
	 * Details describing the properties of this set of consumers.
	 */
	protected final ConsumerProfile												profile;

	/**
	 * A link back to the simulation we are running under. Use of a singleton
	 * would be inappropriate given that Mason initializes multiple instances of
	 * a single simulation simultaneously.
	 */
	protected final Simternet													s;

	protected Temporal<EdgeNetwork>										subscribedTo			= new Temporal<EdgeNetwork>(
																												null);

	/**
	 * Create a new consumer class in simulation s with location, population,
	 * and consumer profile as specified.
	 * 
	 */
	protected AbstractConsumerClass(Simternet s, Int2D location, Double population, ConsumerProfile profile) {
		this.s = s;
		this.location = location;
		this.profile = profile;
		this.population = new Temporal<Double>(population);
		this.name = s.config.getCCName();
	}

	/**
	 * @param asp
	 *            The application to use
	 * @param aen
	 *            The network on which to use the application
	 */
	protected void consumeApplication(ApplicationServiceProvider application, EdgeNetwork network) {
		application.processUsage(this, network);
	}

	/**
	 * This function should, for each application the consumer uses, call
	 * consumeApplication() to actualize that consumption.
	 */
	protected void consumeApplications() {
		if (this.subscribedTo.get() == null)
			return;

		// For Each Category
		for (List<ApplicationServiceProvider> asps : this.appsUsed.values())
			// Each ASP with that category
			for (ApplicationServiceProvider asp : asps) {
				// Use that app on the network we're subscribed to
				this.consumeApplication(asp, this.subscribedTo.get());
				Logger.getRootLogger().log(Level.TRACE, this + " consumed " + asp);
			}
	}

	/**
	 * Maintain usage of the specified network. E.g., pay the bill, but could
	 * include more tasks in the future...
	 * 
	 * TODO: Make price paid reflect utility of consumer?
	 * 
	 * @param network
	 */
	protected void consumeNetwork(EdgeNetwork edge) {
		edge.processUsage(this);
	}

	/**
	 * For each network this consumer agent is connected to, use
	 * consumeNetwork(AbstractEdgeNetwork) to handle the details.
	 */
	protected void consumeNetworks() {
		if (this.subscribedTo.get() != null)
			this.consumeNetwork(this.subscribedTo.get());
	}

	public Int2D getLocation() {
		return this.location;
	}

	/**
	 * @return The TOTAL population of this specific Consumer Class at ALL
	 *         locations.
	 */
	public Double getPopultation() {
		return this.population.get();
	}

	/**
	 * @param aen
	 *            Network
	 * @return If the agent is connected to the specifid network, this function
	 *         returns its population, and zero otherwise.
	 */
	public Double getSubscribers(EdgeNetwork aen) {
		if (this.usesNetwork(aen))
			// per agent subscription is all or nothing.
			return this.getPopultation();
		else
			return 0.0;
	}

	/**
	 * Make decisions about <i>which</i> applications to use. Process their
	 * actual usage in consumeApplications().
	 */
	protected void manageApplications() {

	}

	/**
	 * Make decisions about <i>which</i> networks to use. Process their actual
	 * usage in consumeNetworks();
	 */
	protected abstract void manageNetworks();

	@Override
	public void step(SimState state) {
		Logger.getRootLogger().log(Level.TRACE, "Stepping" + this.toString());

		// Make decisions about consumption
		this.manageNetworks();
		this.manageApplications();

		// Act on those decisions
		this.consumeNetworks();
		this.consumeApplications();
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public void update() {
		this.population.update();
		this.appBudgetConstraints.update();
		this.appsUsed.update();
	}

	public Boolean usesNetwork(EdgeNetwork network) {
		return network.equals(this.subscribedTo.get());
	}

}
