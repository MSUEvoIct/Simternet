package simternet.nsp;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import sim.field.grid.SparseGrid2D;
import simternet.Exogenous;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;

public class RepeatedStackelbergPricingStrategy implements PricingStrategy,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SparseGrid2D networks = null;
	protected AbstractNetworkProvider nsp = null;

	public RepeatedStackelbergPricingStrategy(AbstractNetworkProvider nsp,
			SparseGrid2D networks) {
		this.nsp = nsp;
		this.networks = networks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Double getPrice(Class<? extends AbstractNetwork> cl,
			AbstractConsumerClass cc, int x, int y) {

		if (this.networks == null)
			return null;

		Iterator<AbstractNetwork> nets = this.networks.getObjectsAtLocation(x,
				y).iterator();

		while (nets.hasNext()) {
			AbstractNetwork an = nets.next();
			if (cl.isInstance(an))
				return an.getPrice(cc);
		}
		return null;
	}

	private void setPrice(AbstractNetwork an) {
		Set<AbstractConsumerClass> consumerClasses = this.nsp.simternet
				.getConsumerClasses();
		for (AbstractConsumerClass cc : consumerClasses) {
			Double totPopAtLoc = cc.getPopulation(an.getLocationX(), an
					.getLocationY());
			Double othersQty = cc.getTotalLocalSubscriptions(an.getClass(), an
					.getLocationX(), an.getLocationY())
					- an.getCustomers(cc);
			Double price = (Exogenous.maxPrice * (-1 * othersQty + totPopAtLoc))
					/ (2 * totPopAtLoc);
			an.setPrice(cc, price);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPrices() {
		Iterator<AbstractNetwork> i = this.networks.iterator();
		while (i.hasNext())
			// set price for all networks
			this.setPrice(i.next());
	}

}
