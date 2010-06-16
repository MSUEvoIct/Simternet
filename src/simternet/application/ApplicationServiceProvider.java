package simternet.application;

import java.io.Serializable;
import java.util.UUID;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Financials;
import simternet.Simternet;
import simternet.network.NetFlow;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;

public class ApplicationServiceProvider implements Steppable, Serializable,
		AsyncUpdate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Application applicationOffered;
	protected Financials financials;
	protected String name = UUID.randomUUID().toString();
	protected Temporal<Double> priceAdvertising = new Temporal<Double>(3.0);
	protected Temporal<Double> priceSubscriptions = new Temporal<Double>(3.0);
	protected Temporal<Double> revenueAdvertising = new Temporal<Double>(0.0);
	protected Temporal<Double> revenueSubscriptions = new Temporal<Double>(0.0);
	protected Simternet s;

	public ApplicationServiceProvider(Simternet s) {
		this.s = s;
		this.financials = new Financials(s, 10000.0);
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
	 * @param amount
	 *            How much has the application been used? TODO: Think about
	 *            units.
	 * @param acc
	 *            The consumer class which is using the application
	 * @param originLocation
	 *            Where the usage originates from
	 * @param datacenterLocation
	 *            Where the usage was serviced/processed from
	 */
	public void processUsage(final NetFlow usage) {
		double ads = usage.amount * this.priceAdvertising.get();
		double sub = usage.amount * this.priceSubscriptions.get();
		this.revenueAdvertising.increment(ads);
		this.revenueSubscriptions.increment(sub);
		this.financials.earn(ads + sub);
	}

	@Override
	public void step(SimState state) {
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
	}

}
