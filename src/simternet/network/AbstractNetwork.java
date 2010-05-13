package simternet.network;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import simternet.consumer.AbstractConsumerClass;
import simternet.nsp.AbstractNetworkProvider;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.TemporalHashMap;

public abstract class AbstractNetwork implements AsyncUpdate, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Map<AbstractConsumerClass, Double> customers;
	protected Integer locationX;
	protected Integer locationY;
	protected AbstractNetworkProvider nsp;

	public Double billCustomers() {
		Double revenue = 0.0;
		for (Entry<AbstractConsumerClass, Double> e : this.getCustomers()
				.entrySet()) {
			AbstractConsumerClass cc = e.getKey();
			Double numCustomers = e.getValue();
			Double price = this.nsp.getPrice(this.getClass(), cc, this
					.getLocationX(), this.getLocationY());
			revenue += numCustomers * price;
		}
		return revenue;
	}

	public abstract Double getBuildCost();

	public Map<AbstractConsumerClass, Double> getCustomers() {
		return this.customers;
	}

	public Double getCustomers(AbstractConsumerClass cc) {
		Double numCustomers = this.customers.get(cc);
		if (numCustomers == null)
			return 0.0;
		else
			return numCustomers;
	}

	public Integer getLocationX() {
		return this.locationX;
	}

	public Integer getLocationY() {
		return this.locationY;
	}

	/**
	 * @return The price at which the NSP offers service on this network.
	 */
	public abstract Double getPrice(AbstractConsumerClass cc);

	/**
	 * @return The number of customers subscribing to this network from all
	 *         consumer groups.
	 */
	public Double getTotalCustomers() {
		Double numCustomers = 0.0;
		for (Double numCustInConsumerGroup : this.customers.values())
			numCustomers += numCustInConsumerGroup;
		return numCustomers;
	}

	public void init(AbstractNetworkProvider nsp, Integer x, Integer y) {
		this.nsp = nsp;
		this.locationX = x;
		this.locationY = y;
		this.customers = new TemporalHashMap<AbstractConsumerClass, Double>();
	}

	public void setCustomers(AbstractConsumerClass cc, Double numCustomers) {
		this.customers.put(cc, numCustomers);
	}

	public void setLocationX(Integer locationX) {
		this.locationX = locationX;
	}

	public void setLocationY(Integer locationY) {
		this.locationY = locationY;
	}

	public abstract void setPrice(AbstractConsumerClass cc, Double price);

	@Override
	public void update() {
		if (this.customers instanceof TemporalHashMap<?, ?>) {
			TemporalHashMap<AbstractConsumerClass, Double> cthm = (TemporalHashMap<AbstractConsumerClass, Double>) this.customers;
			cthm.update();
		}

	}

}
