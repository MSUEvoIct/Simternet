package simternet.nsp;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
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
			AbstractConsumerClass cc, Int2D location) {

		if (this.networks == null)
			return null;

		Iterator<AbstractNetwork> nets = this.networks.getObjectsAtLocation(
				location.x, location.yy).iterator();

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
			Double totPopAtLoc = cc.getPopulation(an.getLocation().x, an
					.getLocation().y);
			Double othersQty = cc.getTotalLocalSubscriptions(an.getClass(), an
					.getLocation().x, an.getLocation().y)
					- an.getCustomers(cc);
			Double price = (Double.parseDouble(this.nsp.simternet.parameters
					.getProperty("comsumers.simple.maxPrice")) * (-1
					* othersQty + totPopAtLoc))
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
