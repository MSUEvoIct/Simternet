package simternet.network;

import java.util.HashMap;
import java.util.Map;

import simternet.consumer.AbstractConsumerClass;
import simternet.nsp.AbstractNetworkProvider;

public abstract class AbstractNetwork {

	protected AbstractNetworkProvider nsp;
	protected Integer locationX;
	protected Integer locationY;
	protected Map<AbstractConsumerClass,Double> customers;
	
	public void init(AbstractNetworkProvider nsp, Integer x, Integer y) {
		this.nsp = nsp;
		this.locationX = x;
		this.locationY = y;
		customers = new HashMap<AbstractConsumerClass,Double>();
	}
	
	/**
	 * @return The price at which the NSP offers service on this network.
	 */
	public abstract Double getPrice(AbstractConsumerClass cc);
	public abstract void setPrice(AbstractConsumerClass cc, Double price);
	public abstract Double getBuildCost();
	public Double getCustomers(AbstractConsumerClass cc) {
		Double numCustomers = customers.get(cc);
		if (numCustomers == null) 
			return 0.0;
		else
			return numCustomers;
	}
	/**
	 * @return The number of customers subscribing to this network
	 * from all consumer groups.
	 */
	public Double getTotalCustomers() {
		Double numCustomers = 0.0;
		for(Double numCustInConsumerGroup : customers.values()) 
			numCustomers += numCustInConsumerGroup;
		return numCustomers;
	}
	public void setCustomers(AbstractConsumerClass cc, Double numCustomers) {
		this.customers.put(cc, numCustomers);
	}
	public Map<AbstractConsumerClass,Double> getCustomers() {
		return this.customers;
	}

	public Integer getLocationX() {
		return locationX;
	}

	public void setLocationX(Integer locationX) {
		this.locationX = locationX;
	}

	public Integer getLocationY() {
		return locationY;
	}

	public void setLocationY(Integer locationY) {
		this.locationY = locationY;
	}
	
}
