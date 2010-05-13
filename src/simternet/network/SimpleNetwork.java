package simternet.network;

import java.io.Serializable;

import simternet.Exogenous;
import simternet.consumer.AbstractConsumerClass;

public class SimpleNetwork extends AbstractNetwork implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double price = null;

	// Kludge... deepCopy is replicating the PARENT class's items...
	// @Override
	// public SimpleNetwork deepCopy() {
	// // TODO: Test to make sure deepcopy functions as expected
	// SimpleNetwork ret = new SimpleNetwork();
	// ret.init(this.nsp, this.locationX, this.locationY);
	// for (Entry<AbstractConsumerClass, Double> e : this.getCustomers()
	// .entrySet())
	// ret
	// .setCustomers(e.getKey(), new Double(e.getValue()
	// .doubleValue()));
	// ret.setPrice(null, new Double(this.getPrice(null).doubleValue()));
	// return ret;
	// }

	@Override
	public Double getBuildCost() {
		Double cost = 0.0;
		Double population = this.nsp.getSimternet().getPopulation(
				this.locationX, this.locationY);

		cost += Exogenous.netCostSimpleArea;
		cost += population * Exogenous.netCostSimpleUser;

		return cost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simternet.Network#getPrice(simternet.ConsumerClass)
	 * 
	 * There is no price discrimination on a SimpleNetwork.
	 */
	@Override
	public Double getPrice(AbstractConsumerClass cc) {
		return this.price;
	}

	@Override
	public void setPrice(AbstractConsumerClass cc, Double price) {
		this.price = price;
	}

}
