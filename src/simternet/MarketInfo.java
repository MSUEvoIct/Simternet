package simternet;

import java.util.HashMap;
import java.util.Map;

import simternet.consumer.AbstractConsumerClass;
import simternet.nsp.AbstractNetworkProvider;

/**
 * Returns various information about the simternet ICT market that is useful to
 * many agents and does not naturally belong within the methods of a single
 * agent type's class. I.e., how many network providers there are.
 * 
 * @author kkoning
 * 
 */
public class MarketInfo {
	private Simternet	s;

	public MarketInfo(Simternet s) {
		this.s = s;
	}

	/**
	 * @return a map of network service providers and the proportion of market
	 *         share, only including those customers who are subscribed to one
	 *         or more providers.
	 */
	Map<AbstractNetworkProvider, Double> nspsByMarketShare() {
		Map<AbstractNetworkProvider, Double> marketShareMap = this.nspsBySize();
		double totalCustomers = 0.0;
		for (Double customers : marketShareMap.values())
			totalCustomers += customers;
		for (AbstractNetworkProvider anp : marketShareMap.keySet())
			marketShareMap.put(anp, marketShareMap.get(anp) / totalCustomers);
		return marketShareMap;
	}

	/**
	 * @return a map of network service providers and the proportion of market
	 *         share, only including all individual consumers within the system.
	 */
	Map<AbstractNetworkProvider, Double> nspsByPopulationFractionServed() {
		Map<AbstractNetworkProvider, Double> marketShareMap = this.nspsBySize();
		double totalConsumers = this.numberOfConsumers();
		for (AbstractNetworkProvider anp : marketShareMap.keySet())
			marketShareMap.put(anp, marketShareMap.get(anp) / totalConsumers);
		return marketShareMap;
	}

	/**
	 * @return a map of network service providers and their number of customers.
	 */
	Map<AbstractNetworkProvider, Double> nspsBySize() {
		Map<AbstractNetworkProvider, Double> customerMap = new HashMap<AbstractNetworkProvider, Double>();
		for (AbstractNetworkProvider anp : this.s.networkServiceProviders)
			customerMap.put(anp, anp.getCustomers());

		return customerMap;
	}

	/**
	 * @return The total number of agents currently active in the Simternet
	 *         system.
	 */
	Integer numberOfAgents() {
		return this.s.applicationServiceProviders.size() + this.s.networkServiceProviders.size()
				+ this.s.consumerClasses.size();
	}

	/**
	 * @return The total number of application providers currently active in the
	 *         Simternet system.
	 */
	Integer numberOfApplicationProviders() {
		return this.s.applicationServiceProviders.size();
	}

	/**
	 * @return The number of consumer agents active in the Simternet system.
	 */
	Integer numberOfConsumerAgents() {
		return this.s.consumerClasses.size();
	}

	/**
	 * @return The total number of consumers (not agents!) active in the
	 *         Simternet system.
	 */
	Double numberOfConsumers() {
		double numCustomers = 0.0;
		for (Object o : this.s.consumerClasses.allObjects) {
			AbstractConsumerClass acc = (AbstractConsumerClass) o;
			numCustomers += acc.getPopultation();
		}
		return numCustomers;
	}

	Integer numberOfNetworkProviders() {
		return this.s.networkServiceProviders.size();
	}

}