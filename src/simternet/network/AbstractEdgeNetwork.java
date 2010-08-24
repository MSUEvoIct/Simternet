package simternet.network;

import java.util.Iterator;

import sim.util.Bag;
import sim.util.Int2D;
import simternet.consumer.AbstractConsumerClass;
import simternet.nsp.AbstractNetworkProvider;

public abstract class AbstractEdgeNetwork extends AbstractNetwork {

	private static final long serialVersionUID = 1L;
	protected Link upstream;

	@SuppressWarnings("unchecked")
	public Double getNumSubscribers() {
		double customers = 0;

		Bag b = this.owner.simternet.getConsumerClasses().getObjectsAtLocation(
				this.location);
		if (b == null)
			return 0.0;

		Iterator<AbstractConsumerClass> i = b.iterator();

		while (i.hasNext()) {
			AbstractConsumerClass acc = i.next();
			customers += acc.getSubscribers(this);
		}
		return customers;
	}

	public void handleUserTraffic(NetFlow nf) {

	}

	@Override
	public void init(final AbstractNetworkProvider nsp, final Int2D location) {
		super.init(nsp, location);
		// for now, just use the free, infinite network.
		Link l = Link.infiniteLink(this, nsp.getCentralHub());
		this.upstream = l;
		nsp.getCentralHub().createSymmetricLink(l);
	}

	public void receivePayment(AbstractConsumerClass acc, Double numCustomers) {

		double price = this.owner.getPrice(this.getClass(), acc, this.location);
		double revenue = numCustomers * price;

		this.owner.financials.earn(revenue);

	}

}
