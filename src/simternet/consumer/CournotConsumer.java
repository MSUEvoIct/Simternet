package simternet.consumer;

import java.io.Serializable;

import sim.engine.SimState;
import simternet.CournotSimternet;
import simternet.Exogenous;
import simternet.Simternet;
import simternet.network.SimpleNetwork;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.CournotNetworkServiceProvider;

public class CournotConsumer extends SimpleConsumer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CournotConsumer(Simternet s) {
		super(s);
	}

	protected void makePurchaseDecisions() {
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				this.makePurchaseDecisionsAt(x, y);
	}

	protected void makePurchaseDecisionsAt(Integer x, Integer y) {
		this.makePurchaseDecisionsForSimpleNetworkAt(x, y);
	}

	private void makePurchaseDecisionsForSimpleNetworkAt(Integer x, Integer y) {

		for (AbstractNetworkProvider n : this.s.getNetworkServiceProviders()) {
			CournotNetworkServiceProvider nsp = (CournotNetworkServiceProvider) n;
			// Previous code didn't run, might wanna check that
			if (nsp.hasNetworkAt(SimpleNetwork.class, x, y)) {
				int numberOfProviders = this.s.getNetworkServiceProviders()
						.size();
				Double qtyDemanded = new Double((CournotSimternet.ALPHA - nsp
						.getPrice())
						/ numberOfProviders);
				qtyDemanded = new Double((qtyDemanded / CournotSimternet.ALPHA)
						* this.s.getPopulation(x, y));
				// qtyDemanded = new Double(.25 * s.getPopulation(x, y));
				nsp.setCustomers(SimpleNetwork.class, this, x, y, qtyDemanded);
				nsp
						.setTotalSubscribers(nsp.getTotalSubscribers()
								+ qtyDemanded);
			}
		}

	}

	@Override
	public void step(SimState state) {
		this.makePurchaseDecisions();
		for (AbstractNetworkProvider n : this.s.getNetworkServiceProviders()) {
			CournotNetworkServiceProvider nsp = (CournotNetworkServiceProvider) n;
			System.out.println("Market Share: "
					+ (nsp.getTotalSubscribers() / this.s.getPopulation())
					+ "\nPrice: " + nsp.getPrice());
		}

		System.out.println("----------------");
	}

}