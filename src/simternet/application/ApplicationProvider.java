package simternet.application;

import java.io.Serializable;
import java.util.HashSet;

import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Financials;
import simternet.Simternet;
import simternet.TraceConfig;
import simternet.consumer.Consumer;
import simternet.network.Backbone;
import simternet.network.Datacenter;
import simternet.network.EdgeNetwork;
import simternet.network.InteractiveFlow;
import simternet.network.NetFlow;
import simternet.network.Network;
import simternet.network.RoutingProtocolConfig;
import simternet.nsp.NetworkProvider;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;

public class ApplicationProvider implements Steppable, Serializable, AsyncUpdate {
	/**
	 * Other data structures will rely on this not changing, e.g., one that
	 * keeps a lists of ASPs within an App Category.
	 */
	protected final AppCategory			appCategory;

	// TODO: Set this better;
	protected Temporal<Double>			bandwidth				= new Temporal<Double>(100.0);
	protected HashSet<Network>			connectedNetworks		= new HashSet<Network>();
	protected Datacenter				datacenter;
	protected Temporal<Double>			duration				= new Temporal<Double>(100.0);
	protected Financials				financials;
	protected String					name;
	protected Temporal<Double>			priceAdvertising		= new Temporal<Double>(3.0);
	protected Temporal<Double>			priceSubscriptions		= new Temporal<Double>(3.0);
	/**
	 * Quick-and-dirty measure of an application's quality. Should reflect
	 * investment in all qualities other than network transport.
	 */
	protected Temporal<Double>			quality					= new Temporal<Double>(0.0);
	protected QualityStrategy			qualityStrategy			= new QualityStrategy(this);
	protected Temporal<Double>			revenueAdvertising		= new Temporal<Double>(0.0);
	protected Temporal<Double>			revenueSubscriptions	= new Temporal<Double>(0.0);
	public Simternet					s;
	protected TransitPurchaseStrategy	transitStrategy;

	private static final long			serialVersionUID		= 1L;

	public ApplicationProvider(Simternet s, AppCategory appCategory) {
		// housekeeping
		this.s = s;
		this.appCategory = appCategory;
		this.name = s.config.getASPName();
		// TODO: Parameratize ASP endowment.
		this.financials = new Financials(s, 10000.0);

		// Create datacenter, connect it to all NSPs.
		this.datacenter = new Datacenter(this);
	}

	private void connectDatacenter() {
		for (NetworkProvider anp : this.s.getNetworkServiceProviders()) {
			Backbone bn = anp.getBackboneNetwork();
			if (!this.connectedNetworks.contains(bn)) {
				// XXX: hard coded bandwidth!
				this.datacenter.createEgressLinkTo(anp.getBackboneNetwork(), 5.0E7, RoutingProtocolConfig.TRANSIT);
				this.connectedNetworks.add(bn);
			}
		}
	}

	protected NetFlow createNetFlow(Consumer consumer, EdgeNetwork network) {
		NetFlow flow = new InteractiveFlow( // TODO: Vary interactivity
				this.datacenter, // Flow comes from us
				network, // Flow goes to this network
				consumer, // And this consumer
				this.duration.get(), // Flow lasts for this long
				this.bandwidth.get(), // Wants BW, ideally
				// But uses edge flow-control
				this.flowControl(this.getCongestedBandwidth(network)));
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
			return this.bandwidth.get();
		else if (lastPeriod >= this.bandwidth.get())
			return this.bandwidth.get();
		else if (lastPeriod < 1.0)
			return 1.0; // TODO: Parametize minimum BW
		else
			return lastPeriod * 1.1;
	}

	public AppCategory getAppCategory() {
		return this.appCategory;
	}

	public String getAppCategoryString() {
		String categoryString;
		switch (this.appCategory) {
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
		return this.bandwidth.get();
	}

	protected Double getCongestedBandwidth(Network an) {
		Double congestedMaxSeen = this.datacenter.getObservedBandwidth(an);
		if (congestedMaxSeen == null)
			return this.bandwidth.get();
		return congestedMaxSeen;
	}

	/**
	 * If the application is congested on the specified network, return a ratio
	 * equal to the actual (observed) bandwidth over the amount of bandwidth
	 * requested by the application.
	 * 
	 * @param an
	 * @return the congestion ratio
	 */
	public Double getCongestionRatio(Network an) {
		Double congestionRatio;

		Double observedBandwidth = this.getCongestedBandwidth(an);
		Double desiredBandwidth = this.bandwidth.get();
		congestionRatio = observedBandwidth / desiredBandwidth;

		if (congestionRatio > 1.0)
			congestionRatio = 1.0;

		return congestionRatio;
	}

	/*
	 * Utility function used by user interface. May be some privacy issues.
	 */
	public HashSet<Network> getConnectedNetworks() {
		return this.connectedNetworks;
	}

	public double getCustomers() {
		// TODO Get this info; will need to look at all customer objects, so
		// value should be cached.
		// for now, always return 100
		return 100.0;
	}

	public Datacenter getDatacenter() {
		return this.datacenter;
	}

	/*
	 * Utility function used by the user interface. May be some privacy issues.
	 */
	public Datacenter getDataCenter() {
		return this.datacenter;
	}

	public Financials getFinancials() {
		return this.financials;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Extracts the number from the network's name, and returns it
	 * 
	 * @return -1 if number extraction failed, or the number if it succeeded.
	 */
	public int getNumber() {
		int start = this.name.lastIndexOf('-') + 1;
		try {
			int number = Integer.parseInt(this.name.substring(start));
			return number;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public Double getPriceSubscriptions() {
		return this.priceSubscriptions.get();
	}

	public Double getQuality() {
		return this.quality.get();
	}

	public Temporal<Double> getRevenueAdvertising() {
		return this.revenueAdvertising;
	}

	public Temporal<Double> getRevenueSubscriptions() {
		return this.revenueSubscriptions;
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
	 */
	public void processUsage(Consumer consumer, EdgeNetwork network) {

		double ads = this.priceAdvertising.get();
		double sub = this.priceSubscriptions.get();
		this.revenueAdvertising.increase(ads);
		this.revenueSubscriptions.increase(sub);
		this.financials.earn(ads + sub);

		NetFlow flow = this.createNetFlow(consumer, network);

		this.datacenter.originate(flow);
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void step(SimState state) {
		this.connectDatacenter();
		this.datacenter.step(state);
		this.qualityStrategy.investInQuality();

		if (TraceConfig.financialStatusASP && Logger.getRootLogger().isTraceEnabled())
			Logger.getRootLogger().trace(this + " Financials: " + this.financials);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public void update() {
		this.duration.update();
		this.bandwidth.update();
		this.financials.update();
		this.revenueAdvertising.update();
		this.revenueSubscriptions.update();
		this.priceAdvertising.update();
		this.priceSubscriptions.update();
		this.datacenter.update();
		this.quality.update();
	}
}
