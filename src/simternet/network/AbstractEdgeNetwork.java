package simternet.network;

import sim.util.Int2D;
import simternet.consumer.AbstractConsumerClass;
import simternet.nsp.AbstractNetworkProvider;

public abstract class AbstractEdgeNetwork extends AbstractNetwork {

	private static final long serialVersionUID = 1L;
	protected Link upstream;

	public Double getNumCustomers() {
		double customers = 0;

		for (AbstractConsumerClass acc : this.owner.simternet
				.getConsumerClasses())
			customers += acc.numSubscriptions(this);

		return customers;
	}

	@Override
	public void init(final AbstractNetworkProvider nsp, final Int2D location) {
		super.init(nsp, location);
	}

	public void receivePayment(AbstractConsumerClass acc, Double numCustomers) {

		double price = this.owner.getPrice(this.getClass(), acc, this.location);
		double revenue = numCustomers * price;

		this.owner.financials.earn(revenue);

	}

}
