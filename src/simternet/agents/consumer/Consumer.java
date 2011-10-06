package simternet.agents.consumer;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.agents.asp.AppCategory;
import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.behavior.DefaultAppBenefitCalculator;
import simternet.agents.consumer.behavior.DefaultAppCategoryBudgetCalculator;
import simternet.engine.Simternet;
import simternet.engine.StepBooleanSequence;
import simternet.engine.StepDoubleSequence;
import simternet.engine.TraceConfig;
import simternet.engine.asyncdata.AsyncUpdate;
import simternet.engine.asyncdata.Temporal;
import simternet.engine.asyncdata.TemporalHashMap;
import simternet.network.EdgeNetwork;
import simternet.network.NetFlow;

public class Consumer implements Steppable, AsyncUpdate, Serializable {
	// Housekeeping
	private static final long											serialVersionUID		= 1L;
	public final Simternet												s;

	// Consumer Behavior
	/**
	 * Calculates the benefits the consumer receives from the use of each
	 * application. Different consumers and different consumer classes may
	 * preceive the utility of the same application differently. That may be
	 * done through a constistent algorithm acting on differing agent state, or
	 * it may be done through an entirely different algorithm for different
	 * users.
	 */
	protected AppBenefitCalculator										appBenefitCalculator;

	/**
	 * Controls the consumer's application consumption behavior; it controls
	 * which applications the consumer uses. This behavior is specified in a
	 * separate object rather than code within an individual class so that
	 * distinct behaviors can be mixed and matched without refactoring code.
	 */
	protected AppManager												appManager;

	/**
	 * Controls the consumer's network consumption behavior; it controlls which
	 * edge networks the consumer uses. This behavior is specified in a separate
	 * object rather than code within an individual class so that distinct
	 * behaviors can be mixed and matched without refactoring code.
	 */
	protected NetManager												netManager;

	/**
	 * Controls the consumer's allocation of its overall application budget
	 * among the different categories of available applications. It is expected
	 * that consumers could shift expenditures into categories which provide
	 * them with a greater benefit.
	 */
	protected AppCategoryBudgetCalculator								appCatBudgetManager;

	// Consumer State
	/**
	 * A human-readable name to distinguish this consumer agent
	 */
	public String														name;

	/**
	 * The physical location of this set of consumers.
	 */
	public final Int2D													location;

	/**
	 * The number of consumers represented by this agent.
	 */
	protected final Temporal<Double>									population;

	/**
	 * True if this consumer class is an "early adopter."
	 */
	protected boolean													earlyAdopter;

	/**
	 * The maximum price this consumer is willing to pay for a connection to
	 * a/the network. TODO: While intended simply as a way to prevent conumers
	 * from paying (effectively) infinite prices, this may have model
	 * significance; get from a parameter file.
	 */
	private double														maxNetworkPrice			= 100.0;

	/**
	 * The proportion of this period's consumption which is determined by the
	 * last period's consumption. (i.e., momentum or transaction costs) TODO:
	 * Not currently used.
	 */
	protected Double													switchSpeed;

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
	 * The actual list of application service providers selected for use, sorted
	 * by category. manageApplications() is responsible for maintaining this
	 * data structure, based on appBudgetConstraints and the qualities of the
	 * applications themselves.
	 */
	protected TemporalHashMap<AppCategory, List<ApplicationProvider>>	appsUsed				= new TemporalHashMap<AppCategory, List<ApplicationProvider>>();

	/**
	 * The edge network currently used by this set of consumers. In the current
	 * model, consumers may only use one edge network.
	 */
	protected Temporal<EdgeNetwork>										edgeNetwork				= new Temporal<EdgeNetwork>(
																										null);

	// Tracking Variables
	// These should not be used to make decisions for agents; at least, not
	// unless they are limited to data
	// in previous steps (and not the current step).

	/**
	 * The amount of benefit the consumer expects to receive based on its
	 * valuation of applications and the expected congestion of the network.
	 */
	public StepDoubleSequence											benefitRequested;

	/**
	 * The total calculated benefit the consumer has received from applications
	 * per time step.
	 */
	public StepDoubleSequence											benefitReceived;

	/**
	 * The amount paid to network service providers each time period.
	 */
	public StepDoubleSequence											paidToNSPs;

	/**
	 * The amount of transfer capacity that would have been used by the customer
	 * in the absence of any network congestion.
	 */
	public StepDoubleSequence											transferRequested;

	/**
	 * The amount of transfer capacity actually received by the consumer
	 */
	public StepDoubleSequence											transferReceived;

	/**
	 * The number of times this consumer has switched to a different Network
	 * Service Provider
	 */
	public StepBooleanSequence											switchedNSP;

	/**
	 * The number of steps in which this consumer agent decided to consume some
	 * network service.
	 */
	public StepBooleanSequence											usedNSP;

	/**
	 * The number of network flows requested by this customer. Mainly used for
	 * debugging purposes.
	 */
	public StepDoubleSequence											numFlowsRequested;

	/**
	 * The number of network flows actually received by this customer. Mainly
	 * used for debugging purposes.
	 */
	public StepDoubleSequence											numFlowsReceived;

	/**
	 * The number of ASPs the consumer used per time step.
	 */
	public StepDoubleSequence											numASPsUsed;

	// Remember; every Temporal variable needs to be listed here!
	@Override
	public void update() {
		appBudgetConstraints.update();
		appsUsed.update();
		population.update();
		edgeNetwork.update();
	}

	// Top level consumer behavior
	@Override
	public void step(SimState state) {

		// Actualize our consumption decisions from the last time step.
		consumeNetworks();
		consumeApplications();

		// Make decisions about consumption for the next time step.

		// The network decision should be made first, as the applicaiton
		// consumption decisions may be based on it. (e.g., an app may not be
		// available, or may be very congested, on the new network.)
		if (netManager != null) {
			netManager.manageNetworks(this);
		}
		if (appManager != null) {
			appManager.manageApplications(this);
		}
		if (appCatBudgetManager != null) {
			appCatBudgetManager.calculateAppCategoryBudgets(this);
		}

		// Collect data on our current condition
		//

		// are we currently using an NSP?
		boolean usingNSP = true;
		if (edgeNetwork.get() == null) {
			usingNSP = false;
		}
		usedNSP.set(usingNSP);

		// How many apps are we using?
		int numApps = this.getNumAppsUsed();
		numASPsUsed.set(numApps);

	}

	/**
	 * Create a new consumer class in simulation s with location, population,
	 * and consumer profile as specified.
	 * 
	 */
	public Consumer(Simternet s, Int2D location, Double population, NetManager netManager, AppManager appManager,
			AppBenefitCalculator abc, AppCategoryBudgetCalculator acbc) {
		this.s = s;
		// this.name = s.config.getCCName();

		// Initialize data tracking structures
		benefitRequested = new StepDoubleSequence(s);
		benefitReceived = new StepDoubleSequence(s);
		paidToNSPs = new StepDoubleSequence(s);
		transferRequested = new StepDoubleSequence(s);
		transferReceived = new StepDoubleSequence(s);
		numFlowsRequested = new StepDoubleSequence(s);
		numFlowsReceived = new StepDoubleSequence(s);
		numASPsUsed = new StepDoubleSequence(s);
		usedNSP = new StepBooleanSequence(s);
		switchedNSP = new StepBooleanSequence(s);

		if (location != null) {
			this.location = location;
		} else
			throw new RuntimeException("Consumer must have a location");

		if (population != null) {
			this.population = new Temporal<Double>(population);
		} else
			throw new RuntimeException("Consumer must have a population");

		if (netManager != null) {
			this.netManager = netManager;
		}
		if (appManager != null) {
			this.appManager = appManager;
		}

		if (acbc != null) {
			appCatBudgetManager = acbc;
		} else {
			appCatBudgetManager = DefaultAppCategoryBudgetCalculator.getSingleton();
		}

		if (abc != null) {
			appBenefitCalculator = abc;
		} else {
			appBenefitCalculator = DefaultAppBenefitCalculator.getSingleton();
		}
	}

	public static class EdgeNetworkBenefit {
		public double		sumAppBenefits	= 0;
		public EdgeNetwork	network;

		public double density() {
			double density = sumAppBenefits / network.getPriceFuture();
			return density;
		}
	}

	public static class AppBenefit {
		public ApplicationProvider	asp;
		public EdgeNetwork			onNetwork;

		public Double				benefit;
		public Double				cost;

		public Double density() {
			return benefit / cost;
		}

	}

	/**
	 * Actualize the consumption of a particular application on a particular
	 * edge network.
	 * 
	 * @param asp
	 *            The application to be used
	 * @param aen
	 *            The network on which to use the application
	 */
	protected NetFlow consumeApplication(ApplicationProvider application, EdgeNetwork network) {
		return application.processUsage(this, network);
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
				NetFlow flow = consumeApplication(asp, edgeNetwork.get());

				// We need to request a flow to receive this application
				numFlowsRequested.increment();

				// That flow requires a certain amount of bandwidth
				benefitRequested.add(flow.bandwidthRequested);

				// We expect to receive the estimated beenfit
				double expectedBenefit = appBenefitCalculator.estimateBenefit(this, asp, edgeNetwork.get());
				benefitRequested.add(expectedBenefit);

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

		// In order to consume a network, we're going to need to pay the NSP to
		// use it.
		double paidToNSP = edge.getPrice();
		paidToNSPs.add(paidToNSP);
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

		if (TraceConfig.networking.congestionNSPSummary && Logger.getRootLogger().isTraceEnabled()) {
			Logger.getRootLogger().log(Level.TRACE,
					this + " received " + flow + ", congestion = " + flow.describeCongestionForHumans());
		}

		// Update the total transfer received
		transferReceived.add(flow.bandwidth);

		// We have received one additional flow.
		numFlowsReceived.increment();

		// What is the actual benefit we saw?
		double appBenefit = appBenefitCalculator.calculateBenefit(this, flow.getApplicationProvider(),
				flow.getTransferFraction());
		benefitReceived.add(appBenefit);
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + "*" + population.get() + "@" + location;
	}

	public Boolean usesNetwork(EdgeNetwork network) {
		return network.equals(edgeNetwork.get());
	}

	public TemporalHashMap<AppCategory, List<ApplicationProvider>> getAppsUsed() {
		return appsUsed;
	}

	/**
	 * @param category
	 *            The application category
	 * @return The number of applications used by the consumer in a single
	 *         application category
	 */
	public int getNumAppsUsed(AppCategory category) {
		List<ApplicationProvider> apps = appsUsed.get(category);
		return apps.size();
	}

	/**
	 * @return The number of applications used by the consumer in all
	 *         application categories.
	 */
	public int getNumAppsUsed() {
		int numAppsUsed = 0;
		for (List<ApplicationProvider> asps : appsUsed.values()) {
			numAppsUsed = numAppsUsed + asps.size();
		}
		return numAppsUsed;
	}

	public Double getMaxNetworkPrice() {
		return maxNetworkPrice;
	}

	public void setEdgeNetwork(EdgeNetwork en) {
		edgeNetwork.set(en);
	}

	public Temporal<EdgeNetwork> getEdgeNetwork() {
		return edgeNetwork;
	}

	public void setAppsUsed(AppCategory category, List<ApplicationProvider> asps) {
		appsUsed.put(category, asps);
	}

	public Double getApplicationCategoryBudget(AppCategory cat) {
		Double budget = appBudgetConstraints.get(cat);
		if (budget == null)
			return 0.0;
		else
			return budget;
	}

}
