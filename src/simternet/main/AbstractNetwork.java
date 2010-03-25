package simternet.main;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractNetwork {

	protected AbstractNetworkProvider nsp;
	protected Integer locationX;
	protected Integer locationY;
	protected Map<AbstractConsumer,Double> customers;
	
	protected void init(AbstractNetworkProvider nsp, Integer x, Integer y) {
		this.nsp = nsp;
		this.locationX = x;
		this.locationY = y;
		customers = new HashMap<AbstractConsumer,Double>();
	}
	
	/**
	 * @return The price at which the NSP offers service on this network.
	 */
	public abstract Double getPrice(AbstractConsumer cc);
	public abstract void setPrice(AbstractConsumer cc, Double price);
	public abstract Double getBuildCost();
	public Double getCustomers(AbstractConsumer cc) {
		return customers.get(cc);
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
	public void setCustomers(AbstractConsumer cc, Double numCustomers) {
		this.customers.put(cc, numCustomers);
	}
	public Map<AbstractConsumer,Double> getCustomers() {
		return this.customers;
	}
}
