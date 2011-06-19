package simternet;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sim.util.Int2D;
import simternet.consumer.Consumer;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.nsp.NetworkProvider;

/**
 * Returns various information about the simternet ICT market that is useful to
 * many agents and does not naturally belong within the methods of a single
 * agent type's class. I.e., how many network providers there are.
 * 
 * @author kkoning
 * 
 */
public class MarketInfo implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Simternet			s;

	public MarketInfo(Simternet s) {
		this.s = s;
	}

	public Double cheapestEdgeNetwork(Int2D location) {
		return this.cheapestOtherEdgeNetwork(location, null);
	}

	public Double cheapestOtherEdgeNetwork(Int2D location, NetworkProvider nsp) {
		Collection<Network> networks = this.s.getNetworks(null, null, location);
		double lowestprice = Double.MAX_VALUE;
		for (Network n : networks) {
			EdgeNetwork en = (EdgeNetwork) n;
			if (en.getOwner().equals(nsp))
				continue;
			double netprice = en.getPrice();
			if (netprice < lowestprice)
				lowestprice = netprice;
		}

		return lowestprice;
	}

	/**
	 * @return a map of network service providers and the proportion of market
	 *         share, only including those customers who are subscribed to one
	 *         or more providers.
	 */
	public Map<NetworkProvider, Double> nspsByMarketShare() {
		Map<NetworkProvider, Double> marketShareMap = this.nspsBySize();
		double totalCustomers = 0.0;
		for (Double customers : marketShareMap.values())
			totalCustomers += customers;
		for (NetworkProvider anp : marketShareMap.keySet())
			marketShareMap.put(anp, marketShareMap.get(anp) / totalCustomers);
		return marketShareMap;
	}

	/**
	 * @return a map of network service providers and the proportion of market
	 *         share, only including all individual consumers within the system.
	 */
	public Map<NetworkProvider, Double> nspsByPopulationFractionServed() {
		Map<NetworkProvider, Double> marketShareMap = this.nspsBySize();
		double totalConsumers = this.numberOfConsumers();
		for (NetworkProvider anp : marketShareMap.keySet())
			marketShareMap.put(anp, marketShareMap.get(anp) / totalConsumers);
		return marketShareMap;
	}

	/**
	 * @return a map of network service providers and their number of customers.
	 */
	public Map<NetworkProvider, Double> nspsBySize() {
		Map<NetworkProvider, Double> customerMap = new HashMap<NetworkProvider, Double>();
		for (NetworkProvider anp : this.s.networkServiceProviders)
			customerMap.put(anp, anp.getCustomers());

		return customerMap;
	}

	/**
	 * @return The total number of agents currently active in the Simternet
	 *         system.
	 */
	public Integer numberOfAgents() {
		return this.numberOfApplicationProviders() + this.numberOfNetworkProviders() + this.numberOfConsumerAgents();
	}

	/**
	 * @return The total number of application providers currently active in the
	 *         Simternet system.
	 */
	public Integer numberOfApplicationProviders() {
		return this.s.applicationProviders.size();
	}

	/**
	 * @return The number of consumer agents active in the Simternet system.
	 */
	public Integer numberOfConsumerAgents() {
		return this.s.getNumConsumerAgents();
	}

	/**
	 * @return The total number of consumers (not agents!) active in the
	 *         Simternet system.
	 */
	public Double numberOfConsumers() {
		double numCustomers = 0.0;
		for (Object o : this.s.consumerAgents.allObjects) {
			Consumer acc = (Consumer) o;
			numCustomers += acc.getPopulation();
		}
		return numCustomers;
	}

	public Integer numberOfNetworkProviders() {
		return this.s.networkServiceProviders.size();
	}

}
