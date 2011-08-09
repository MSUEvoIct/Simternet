package simternet.consumer;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.Simternet;
import simternet.TraceConfig;
import simternet.application.AppCategory;
import simternet.application.ApplicationProvider;
import simternet.network.EdgeNetwork;
import simternet.network.NetFlow;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;
import simternet.temporal.TemporalHashMap;

@SuppressWarnings("serial")
public class Consumer implements Steppable, AsyncUpdate, Serializable {

	protected AppBenefitCalculator										appBenefitCalculator	= AppBenefitCalculator
																										.getSingleton();
	/**
	 * Consumers do not consume all applications. Whether the budget constraint
	 * is measured in time or dollars, consumers will not spend more than this
	 * amount on the app class during the next period. To the extent that those
	 * preferences change over time (i.e., consumers tending to spend more time
	 * consuming entertainment content), this should be reflected by changing
	 * these values.
	 */
	protected TemporalHashMap<AppCategory, Double>						appBudgetConstraints	= new TemporalHashMap<AppCategory, Double>();

	/**
	 * Controls the consumer's application consumption behavior; it controls
	 * which applications the consumer uses. This behavior is specified in a
	 * separate object rather than code within an individual class so that
	 * distinct behaviors can be mixed and matched without refactoring code.
	 */
	protected AppManager												appManager				= AppManager
																										.getSingleton();

	/**
	 * The actual list of application service providers selected for use, sorted
	 * by category. manageApplications() is responsible for maintaining this
	 * data structure, based on appBudgetConstraints and the qualities of the
	 * applications themselves.
	 */
	protected TemporalHashMap<AppCategory, List<ApplicationProvider>>	appsUsed				= new TemporalHashMap<AppCategory, List<ApplicationProvider>>();

	public Double														benefitSeen;

	public double														congestionSeen			= 0.0;
	protected Temporal<EdgeNetwork>										edgeNetwork				= new Temporal<EdgeNetwork>(
																										null);

	/**
	 * The physical location of this set of consumers.
	 */
	protected final Int2D												location;

	/**
	 * A human-readable name to distinguish this consumer agent
	 */
	protected String													name;

	/**
	 * Controls the consumer's network consumption behavior; it controlls which
	 * edge networks the consumer uses. This behavior is specified in a separate
	 * object rather than code within an individual class so that distinct
	 * behaviors can be mixed and matched without refactoring code.
	 */
	protected NetManager												netManager				= NetManager
																										.getSingleton();

	public Double														paidToNSPs;

	/**
	 * The number of consumers represented by this agent.
	 */
	protected final Temporal<Double>									population;

	/**
	 * Details describing the properties of this set of consumers.
	 */
	protected ConsumerProfile											profile					= ConsumerProfile
																										.getSingleton();

	/**
	 * A link back to the simulation we are running under. Use of a singleton
	 * would be inappropriate given that Mason initializes multiple instances of
	 * a single simulation simultaneously.
	 */
	protected final Simternet											s;

	public Double														transferActual			= 0.0;

	public Double														transferRequested		= 0.0;

	/**
	 * Create a new consumer class in simulation s with location, population,
	 * and consumer profile as specified.
	 * 
	 */
	public Consumer(Simternet s, Int2D location, Double population, ConsumerProfile profile, NetManager netManager,
			AppManager appManager, AppBenefitCalculator abc) {
		this.s = s;
		// this.name = s.config.getCCName();

		if (location != null) {
			this.location = location;
		} else
			throw new RuntimeException("Consumer must have a location");

		if (population != null) {
			this.population = new Temporal<Double>(population);
		} else
			throw new RuntimeException("Consumer must have a population");

		if (profile != null) {
			this.profile = profile;
		}
		if (netManager != null) {
			this.netManager = netManager;
		}
		if (appManager != null) {
			this.appManager = appManager;
		}
		if (abc != null) {
			appBenefitCalculator = abc;
		}
	}

	/**
	 * @param asp
	 *            The application to use
	 * @param aen
	 *            The network on which to use the application
	 */
	protected void consumeApplication(ApplicationProvider application, EdgeNetwork network) {
		application.processUsage(this, network);
	}

	/**
	 * This function should, for each application the consumer uses, call
	 * consumeApplication() to actualize that consumption.
	 */
	protected void consumeApplications() {

		// if we are not subscribed to a network, we cannot consume any network
		// applications
		if (edgeNetwork.get() == null)
			return;

		// For Each Category
		for (List<ApplicationProvider> asps : appsUsed.values()) {
			// Each ASP with that category
			for (ApplicationProvider asp : asps) {
				// Use that app on the network we're subscribed to
				consumeApplication(asp, edgeNetwork.get());
				if (TraceConfig.consumerUsedApp && Logger.getRootLogger().isTraceEnabled()) {
					Logger.getRootLogger().trace(this + " consumed " + asp);
				}
			}
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
		if (edgeNetwork.get() != null) {
			consumeNetwork(edgeNetwork.get());
		}
	}

	public AppBenefitCalculator getAppBenefitCalculator() {
		return appBenefitCalculator;
	}

	public Int2D getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return The TOTAL population of this specific Consumer Class at ALL
	 *         locations.
	 */
	public Double getPopulation() {
		return population.get();
	}

	/**
	 * @param aen
	 *            Network
	 * @return If the agent is connected to the specific network, this function
	 *         returns its population, and zero otherwise.
	 */
	public Double getSubscribers(EdgeNetwork aen) {
		if (usesNetwork(aen))
			// per agent subscription is all or nothing.
			return getPopulation();
		else
			return 0.0;
	}

	/**
	 * @param asp
	 * @return True if this consumer is subscribed to this ASP
	 */
	public boolean isSubscriber(ApplicationProvider asp) {
		AppCategory category = asp.getAppCategory();
		List<ApplicationProvider> categoryList = appsUsed.get(category);
		if (categoryList == null)
			return false;
		else
			return categoryList.contains(asp);
	}

	/**
	 * The flow is ultimately received by the consumer, and statistics about the
	 * flow are sent to NSPs and ASPs.
	 * 
	 * @param flow
	 */
	public void receiveFlow(NetFlow flow) {

		if (TraceConfig.consumerFlowReceived && Logger.getRootLogger().isTraceEnabled()) {
			Logger.getRootLogger().log(Level.TRACE,
					this + " received " + flow + ", congestion = " + flow.describeCongestion());
		}

		transferRequested += flow.getTransferRequested();
		transferActual += flow.getTransferActual();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void step(SimState state) {
		if (TraceConfig.steppingConsumer && Logger.getRootLogger().isTraceEnabled()) {
			Logger.getRootLogger().trace("Stepping" + toString());
		}

		// Make decisions about consumption
		if (appManager != null) {
			appManager.manageApplications(this);
		}
		if (netManager != null) {
			netManager.manageNetworks(this);
		}

		// Act on those decisions
		consumeNetworks();
		consumeApplications();
	}

	@Override
	public String toString() {
		return name + "*" + population.get() + "@" + location;
	}

	@Override
	public void update() {
		appBudgetConstraints.update();
		appsUsed.update();
		population.update();
		edgeNetwork.update();
	}

	public Boolean usesNetwork(EdgeNetwork network) {
		return network.equals(edgeNetwork.get());
	}

}
