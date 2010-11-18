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

	protected Application applicationOffered;
	protected HashSet<AbstractNetwork> connectedNetworks = new HashSet<AbstractNetwork>();
	protected Datacenter datacenter;
	protected Financials financials;
	protected String name;
	protected Temporal<Double> priceAdvertising = new Temporal<Double>(3.0);
	protected Temporal<Double> priceSubscriptions = new Temporal<Double>(3.0);
	protected Temporal<Double> revenueAdvertising = new Temporal<Double>(0.0);
	protected Temporal<Double> revenueSubscriptions = new Temporal<Double>(0.0);

	protected Simternet s;

	public ApplicationServiceProvider(Simternet s) {
		this.s = s;

		this.name = s.parameters.getASPName();

		// Create datacenter, connect it to all NSPs.
		this.datacenter = new Datacenter(this);

		this.financials = new Financials(s, 10000.0);
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
		NetFlow flow = new InteractiveFlow(this.datacenter, network, consumer,
				100.0, 1000.0, null);
		return flow;
	}

	public Application getApplicationOffered() {
		return this.applicationOffered;
	}

	public Financials getFinancials() {
		return this.financials;
	}

	public String getName() {
		return this.name;
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
		System.out.println("Stepping " + this.getName() + ", has "
				+ this.financials);
	}

	@Override
	public void update() {
		this.financials.update();
		this.revenueAdvertising.update();
		this.revenueSubscriptions.update();
		this.priceAdvertising.update();
		this.priceSubscriptions.update();
		this.datacenter.update();
	}

}
