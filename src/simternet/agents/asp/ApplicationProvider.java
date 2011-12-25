package simternet.agents.asp;

import java.util.HashSet;

import sim.engine.SimState;
import sim.util.Bag;
import simternet.agents.consumer.Consumer;
import simternet.agents.finance.Financials;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Firm;
import simternet.engine.Simternet;
import simternet.engine.TraceConfig;
import simternet.engine.asyncdata.Temporal;
import simternet.network.Backbone;
import simternet.network.DataCenter;
import simternet.network.EdgeNetwork;
import simternet.network.NetFlow;
import simternet.network.Network;
import simternet.network.RoutingProtocolConfig;
import simternet.network.UserInteractiveFlow;

public class ApplicationProvider implements Firm {
	private static final long			serialVersionUID		= 1L;

	// "Housekeeping" information
	public Simternet					s;
	
	/**
	 * Other data structures will rely on this not changing, e.g., one that
	 * keeps a lists of ASPs within an App Category.
	 */
	protected final AppCategory			appCategory;
	protected String					name;

	// Strategies - How the ASP makes its decisions
	protected QualityStrategy			qualityStrategy			= new QualityStrategy(this);
	protected TransitPurchaseStrategy	transitStrategy;

	// ASP State
	// Properties of the Application (things that matter to consumers directly)
	protected Temporal<Double>			quality					= new Temporal<Double>(0.0);
	protected Temporal<Double>			bandwidth				= new Temporal<Double>(100.0);
	protected Temporal<Double>			duration				= new Temporal<Double>(100.0);
	protected Temporal<Double>			priceAdvertising		= new Temporal<Double>(3.0);
	protected Temporal<Double>			priceSubscriptions		= new Temporal<Double>(3.0);

	// Network Information
	protected DataCenter				datacenter;
	protected HashSet<Network>			connectedNetworks		= new HashSet<Network>();

	// Financial Information
	protected Financials				financials;
	protected Temporal<Double>			revenueAdvertising		= new Temporal<Double>(0.0);
	protected Temporal<Double>			revenueSubscriptions	= new Temporal<Double>(0.0);

	// Cached information; things that shouldn't change, are frequently
	// accessed, and expensive to collect.
	protected Double					cachedNumCustomers;

	/**
	 * A random preference matching with Consumer.diversityFactor
	 */
	public double						diversityFactor			= 1.0;

	public ApplicationProvider(Simternet s, AppCategory appCategory) {
		this.s = s;
		this.appCategory = appCategory;

		// TODO: Parameratize ASP endowment.
		financials = new Financials(s, s.config.aspEndowment);

		// Create datacenter, connect it to all NSPs.
		datacenter = new DataCenter(this);

		diversityFactor = s.random.nextDouble();
	}

	private void connectDatacenter() {
		for (NetworkProvider anp : s.getNetworkServiceProviders()) {
			Backbone bn = anp.getBackboneNetwork();
			if (!connectedNetworks.contains(bn)) {
				// XXX: hard coded bandwidth!
				datacenter.createEgressLinkTo(anp.getBackboneNetwork(), 5.0E7, RoutingProtocolConfig.TRANSIT);
				connectedNetworks.add(bn);
			}
		}
	}

	protected NetFlow createNetFlow(Consumer consumer, EdgeNetwork network) {
		NetFlow flow = new UserInteractiveFlow(datacenter, // Flow comes from us
				network, // Flow goes to this network
				consumer, // And this consumer
				bandwidth.get(), duration.get() // Flow lasts for this long
		);
		return flow;
	}

	/**
	 * TODO: Make the per-step increase (curently hard-coded at 10%) a
	 * parameter.
	 * 
	 * @param lastPeriod
	 * @return The bandwidth to use this period.
	 */
	protected Double flowControl(Double lastPeriod) {
		if (lastPeriod == null)
			return bandwidth.get();
		else if (lastPeriod >= bandwidth.get())
			return bandwidth.get();
		else if (lastPeriod < 1.0)
			return 1.0; // TODO: Parametize minimum BW
		else
			return lastPeriod * 1.1;
	}

	public AppCategory getAppCategory() {
		return appCategory;
	}

	public String getAppCategoryString() {
		String categoryString;
		switch (appCategory) {
		case COMMUNICATION:
			categoryString = "Communication";
			break;
		case ENTERTAINMENT:
			categoryString = "Entertainment";
			break;
		case INFORMATION:
			categoryString = "Information";
			break;
		default:
			categoryString = "Undefined";
			break;
		}
		return categoryString;
	}

	public Double getBandwidth() {
		return bandwidth.get();
	}

	protected Double getCongestedBandwidth(Network an) {
		Double congestedMaxSeen = datacenter.getObservedBandwidth(an);
		if (congestedMaxSeen == null)
			return bandwidth.get();
		return congestedMaxSeen;
	}

	/**
	 * This is a delegate method for DataCenter.getFractionExpected, exposed for
	 * use by other agents.
	 * 
	 * @param an
	 * @return the congestion ratio
	 */
	public Double getFractionExpected(EdgeNetwork en) {
		return datacenter.getFractionExpected(en);
	}

	/*
	 * Utility function used by user interface. May be some privacy issues.
	 */
	public HashSet<Network> getConnectedNetworks() {
		return connectedNetworks;
	}

	/**
	 * @return The number of all customers (in population, not # objects) which
	 *         are currently subscribed to this ASP.
	 */
	public double getCustomers() {

		if (cachedNumCustomers != null)
			return cachedNumCustomers;

		double numCustomers = 0.0;

		/*
		 * The info is stored in consumer objects. We need to ask all consumers
		 * whether they use this ASP.
		 */
		Bag consumerBag = s.getConsumerClasses().allObjects;
		int size = consumerBag.numObjs;

		for (int i = 0; i < size; i++) {
			Consumer c = (Consumer) consumerBag.objs[i];
			boolean isSubscribed = c.isSubscriber(this);
			if (isSubscribed) {
				numCustomers += c.getPopulation();
			}
		}

		cachedNumCustomers = new Double(numCustomers);

		return numCustomers;
	}

	public DataCenter getDatacenter() {
		return datacenter;
	}

	public Financials getFinancials() {
		return financials;
	}

	public String getName() {
		return name;
	}

	/**
	 * Extracts the number from the network's name, and returns it
	 * 
	 * @return -1 if number extraction failed, or the number if it succeeded.
	 */
	public int getNumber() {
		int start = name.lastIndexOf('-') + 1;
		try {
			int number = Integer.parseInt(name.substring(start));
			return number;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * @return How many NSPs this ASP is connected to.
	 */
	public int getNumConnectedNetworks() {
		return connectedNetworks.size();
	}

	public Double getPriceSubscriptions() {
		return priceSubscriptions.get();
	}

	public Double getQuality() {
		return quality.get();
	}

	public Temporal<Double> getRevenueAdvertising() {
		return revenueAdvertising;
	}

	public Temporal<Double> getRevenueSubscriptions() {
		return revenueSubscriptions;
	}

	/**
	 * This function is called by the users or the network when the application
	 * is used. In addition to doing billing for the usage, it may track
	 * information for use in other decision making, such as where to locate
	 * datacenters
	 * 
	 * @param acc
	 *            The consumer class which is using the application
	 * @param originLocation
	 *            Where the usage originates from
	 * @param datacenterLocation
	 *            Where the usage was serviced/processed from
	 * @return The Netflow created by this consumption
	 */
	public NetFlow processUsage(Consumer consumer, EdgeNetwork network) {

		double ads = priceAdvertising.get();
		double sub = priceSubscriptions.get();
		double popSize = consumer.getPopulation();
		double adRev = ads * popSize;
		double subRev = sub * popSize;
		revenueAdvertising.increase(adRev);
		revenueSubscriptions.increase(subRev);
		financials.earn(adRev + subRev);

		NetFlow flow = createNetFlow(consumer, network);

		datacenter.originate(flow);
		return flow;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void step(SimState state) {
		if (TraceConfig.programFlow) {
			TraceConfig.out.println("Stepping " + this);
		}

		connectDatacenter();
		datacenter.step(state);
		qualityStrategy.investInQuality();

		if (TraceConfig.financialStatusASP) {
			TraceConfig.out.println(this + " Financials: " + financials);
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void update() {
		duration.update();
		bandwidth.update();
		financials.update();
		revenueAdvertising.update();
		revenueSubscriptions.update();
		priceAdvertising.update();
		priceSubscriptions.update();
		datacenter.update();
		quality.update();
		cachedNumCustomers = null;
	}
}
