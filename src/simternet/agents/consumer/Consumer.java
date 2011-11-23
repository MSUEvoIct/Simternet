package simternet.agents.consumer;

import java.io.Serializable;
import java.util.List;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.agents.asp.AppCategory;
import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.behavior.DefaultAppBenefitCalculator;
import simternet.agents.consumer.behavior.DefaultAppCategoryBudgetCalculator;
import simternet.engine.Simternet;
import simternet.engine.TraceConfig;
import simternet.engine.asyncdata.AsyncUpdate;
import simternet.engine.asyncdata.Temporal;
import simternet.engine.asyncdata.TemporalHashMap;
import simternet.network.EdgeNetwork;
import simternet.network.NetFlow;

public class Consumer implements Steppable, AsyncUpdate, Serializable {
	// Housekeeping
	private static final long											serialVersionUID				= 1L;
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
	private double														maxNetworkPrice					= 100.0;

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
	protected TemporalHashMap<AppCategory, Double>						appBudgetConstraints			= new TemporalHashMap<AppCategory, Double>();

	/**
	 * The actual list of application service providers selected for use, sorted
	 * by category. manageApplications() is responsible for maintaining this
	 * data structure, based on appBudgetConstraints and the qualities of the
	 * applications themselves.
	 */
	protected TemporalHashMap<AppCategory, List<ApplicationProvider>>	appsUsed						= new TemporalHashMap<AppCategory, List<ApplicationProvider>>();

	/**
	 * The edge network currently used by this set of consumers. In the current
	 * model, consumers may only use one edge network.
	 */
	protected Temporal<EdgeNetwork>										edgeNetwork						= new Temporal<EdgeNetwork>(
																												null);

	public double														networkBenefitExponentVariance	= 0.0;
	public double														networkCostExponentVariance		= 0.0;

	// Tracking Variables
	public Temporal<Double>												transferRequested;
	public Temporal<Double>												transferReceived;
	public Temporal<Double>												benefitReceived;
	public Temporal<Double>												paidToNSPs;

	/**
	 * XXX: A random preference for the diversityFactor in Applications.
	 */
	public double														diversityFactor					= 1.0;

	// Remember; every Temporal variable needs to be listed here!
	@Override
	public void update() {
		if (TraceConfig.updates) {
			TraceConfig.out.println("Updating temporal variables for " + this);
		}

		appBudgetConstraints.update();
		appsUsed.update();
		population.update();
		edgeNetwork.update();

		transferRequested.update();
		transferReceived.update();
		benefitReceived.update();
		paidToNSPs.update();

	}

	// Top level consumer behavior
	@Override
	public void step(SimState state) {
		if (TraceConfig.programFlow) {
			TraceConfig.out.println("Entering " + this + ".step()");
		}

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

		if (TraceConfig.programFlow) {
			TraceConfig.out.println("Leaving " + this + ".step()");
		}
	}

	/**
	 * Create a new consumer class in simulation s with location, population,
	 * and consumer profile as specified.
	 * 
	 */
	public Consumer(Simternet s, Int2D location, Double population, NetManager netManager, AppManager appManager,
			AppBenefitCalculator abc, AppCategoryBudgetCalculator acbc) {
		this.s = s;
		transferRequested = new Temporal<Double>(0.0, 0.0);
		transferReceived = new Temporal<Double>(0.0, 0.0);
		benefitReceived = new Temporal<Double>(0.0, 0.0);
		paidToNSPs = new Temporal<Double>(0.0, 0.0);

		// this.name = s.config.getCCName();
		if (TraceConfig.agentInit) {
			TraceConfig.out.println("Initializing " + this);
		}

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

		// Initialize random variance in valuation of network costs and
		// benefits.
		networkBenefitExponentVariance = s.random.nextDouble() * 2 - 1;
		networkBenefitExponentVariance *= s.config.networkValueExponentVarianceRange;
		networkCostExponentVariance = s.random.nextDouble() * 2 - 1;
		networkCostExponentVariance *= s.config.networkValueExponentVarianceRange;

		diversityFactor = s.random.nextDouble();

		if (TraceConfig.agentInit) {
			TraceConfig.out.println(this + ".location initialized to " + location);
			TraceConfig.out.println(this + ".population initialized to " + population);
			TraceConfig.out.println(this + ".earlyAdopter initialized to " + earlyAdopter);
			TraceConfig.out.println(this + ".appBenefitCalculator initialized to " + appBenefitCalculator);
			TraceConfig.out.println(this + ".appManager initialized to " + appManager);
			TraceConfig.out.println(this + ".netManager initialized to " + netManager);
			TraceConfig.out.println(this + ".networkBenefitExponentVariance initialized to "
					+ networkBenefitExponentVariance);
			TraceConfig.out
					.println(this + ".networkCostExponentVariance initialized to " + networkCostExponentVariance);
			TraceConfig.out.println(this + ".diversityFactor initialized to " + diversityFactor);
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
	 * @param asp
	 *            The application to use
	 * @param aen
	 *            The network on which to use the application
	 */
	protected void consumeApplication(ApplicationProvider application, EdgeNetwork network) {
		if (TraceConfig.ops.consumerActions) {
			TraceConfig.out.println("Consuming app of " + application);
		}

		application.processUsage(this, network);
	}

	/**
	 * This function should, for each application the consumer uses, call
	 * consumeApplication() to actualize that consumption.
	 */
	protected void consumeApplications() {
		if (TraceConfig.programFlow) {
			TraceConfig.out.println("Entering consumeApplications()");
		}

		// if we are not subscribed to a network, we cannot consume any network
		// applications
		if (edgeNetwork.get() == null) {
			if (TraceConfig.ops.consumerActions) {
				TraceConfig.out.println("Not subscribed to any edge network; cannot consume");
			}

			return;
		}

		// For Each Category
		for (AppCategory appCat : appsUsed.keySet()) {
			List<ApplicationProvider> asps = appsUsed.get(appCat);

			if (TraceConfig.ops.consumerActions) {
				TraceConfig.out.println("Consuming subscribed apps in category " + appCat);
			}

			// Each ASP with that category
			for (ApplicationProvider asp : asps) {
				// Use that app on the network we're subscribed to
				consumeApplication(asp, edgeNetwork.get());
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
		if (TraceConfig.ops.consumerActions) {
			TraceConfig.out.println(this + ".consumeNetwork(" + edge + ")");
		}

		paidToNSPs.increase(edge.getPrice());

		edge.processUsage(this);
	}

	/**
	 * For each network this consumer agent is connected to, use
	 * consumeNetwork(AbstractEdgeNetwork) to handle the details.
	 */
	protected void consumeNetworks() {
		if (TraceConfig.ops.consumerActions) {
			TraceConfig.out.println(this + " consuming networks");
		}

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
	 * @return the number of applications currently in use by this consumer.
	 */
	public int getNumAppsUsed() {
		int numApps = 0;
		for (AppCategory cat : AppCategory.values()) {
			List<ApplicationProvider> asps = appsUsed.get(cat);
			if (asps != null) {
				numApps += asps.size();
			}
		}
		return numApps;
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
		transferRequested.increase(flow.getRequestedTransfer());
		transferReceived.increase(flow.getActualTransfer());

		double benefitReceived = appBenefitCalculator.calculateBenefit(this, flow.getApplicationProvider(),
				flow.getTransferFraction());

		this.benefitReceived.increase(benefitReceived);

		if (TraceConfig.networking.consumerFlowReceived) {
			TraceConfig.out.println(this + " received flow " + flow + ", transfer received/requested = "
					+ flow.getActualTransfer() + "/" + flow.getRequestedTransfer());
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + "*" + Simternet.nf.format(population.get()) + "@" + location.x + "," + location.y;
	}

	public Boolean usesNetwork(EdgeNetwork network) {
		return network.equals(edgeNetwork.get());
	}

	public TemporalHashMap<AppCategory, List<ApplicationProvider>> getAppsUsed() {
		return appsUsed;
	}

	public Double getMaxNetworkPrice() {
		return maxNetworkPrice;
	}

	public void setEdgeNetwork(EdgeNetwork en) {
		edgeNetwork.set(en);

		if (TraceConfig.kitchenSink) {
			TraceConfig.out.println(this + " edge network set to " + en);
		}
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
