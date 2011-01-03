package simternet.application;

import java.io.Serializable;
import java.util.HashSet;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Financials;
import simternet.Simternet;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;
import simternet.network.BackboneNetwork;
import simternet.network.Datacenter;
import simternet.network.InteractiveFlow;
import simternet.network.NetFlow;
import simternet.network.RoutingProtocolConfig;
import simternet.nsp.AbstractNetworkProvider;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;

public class ApplicationServiceProvider implements Steppable, Serializable,
		AsyncUpdate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Other data structures will rely on this not changing, e.g., one that
	 * keeps a lists of ASPs within an App Category.
	 */
	protected final AppCategory appCategory;
	// TODO: Set this better;
	protected Temporal<Double> bandwidth = new Temporal<Double>(100.0);
	protected HashSet<AbstractNetwork> connectedNetworks = new HashSet<AbstractNetwork>();
	protected Datacenter datacenter;
	protected Temporal<Double> duration = new Temporal<Double>(100.0);
	protected Financials financials;
	protected String name;
	protected Temporal<Double> priceAdvertising = new Temporal<Double>(3.0);
	protected Temporal<Double> priceSubscriptions = new Temporal<Double>(3.0);
	/**
	 * Quick-and-dirty measure of an application's quality. Should reflect
	 * investment in all qualities other than network transport.
	 */
	protected Temporal<Double> quality = new Temporal<Double>(0.0);
	protected Temporal<Double> revenueAdvertising = new Temporal<Double>(0.0);
	protected Temporal<Double> revenueSubscriptions = new Temporal<Double>(0.0);

	protected Simternet s;

	public ApplicationServiceProvider(Simternet s, AppCategory appCategory) {
		// housekeeping
		this.s = s;
		this.appCategory = appCategory;
		this.name = s.parameters.getASPName();
		// TODO: Parameratize ASP endowment.
		this.financials = new Financials(s, 10000.0);

		// Create datacenter, connect it to all NSPs.
		this.datacenter = new Datacenter(this);
	}

	private void connectDatacenter() {
		for (AbstractNetworkProvider anp : this.s.getNetworkServiceProviders()) {
			BackboneNetwork bn = anp.getBackboneNetwork();
			if (!this.connectedNetworks.contains(bn)) {
				this.datacenter.createEgressLinkTo(anp.getBackboneNetwork(),
						null, RoutingProtocolConfig.TRANSIT);
				this.connectedNetworks.add(bn);
			}
		}
	}

	protected NetFlow createNetFlow(AbstractConsumerClass consumer,
			AbstractEdgeNetwork network) {
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

	protected Double getCongestedBandwidth(AbstractNetwork an) {
		Double congestedMaxSeen = this.datacenter.getCongestion(an);
		if (congestedMaxSeen == null)
			return this.bandwidth.get();
		return congestedMaxSeen;
	}

	/**
	 * TODO: Add some sanity checks / debug statements
	 * 
	 * @param an
	 * @return
	 */
	public Double getCongestionRatio(AbstractNetwork an) {
		Double congested = this.getCongestedBandwidth(an);
		Double maxBW = this.bandwidth.get();
		return congested / maxBW;
	}

	public Financials getFinancials() {
		return this.financials;
	}

	public String getName() {
		return this.name;
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
	 * For now, just increase quality by a small, random number.
	 * 
	 * TODO: A non-rediculous heuristic
	 * 
	 */
	protected void investInQuality() {
		Double toInvest = this.s.random.nextDouble() * 10;
		this.quality.increment(toInvest);
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
	public void processUsage(AbstractConsumerClass consumer,
			AbstractEdgeNetwork network) {

		double ads = this.priceAdvertising.get();
		double sub = this.priceSubscriptions.get();
		this.revenueAdvertising.increment(ads);
		this.revenueSubscriptions.increment(sub);
		this.financials.earn(ads + sub);

		NetFlow flow = this.createNetFlow(consumer, network);

		this.datacenter.originate(flow);
	}

	@Override
	public void step(SimState state) {
		this.connectDatacenter();
		this.datacenter.step(state);
		this.investInQuality();
		System.out.println("Stepping " + this.getName() + ", has "
				+ this.financials);
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
