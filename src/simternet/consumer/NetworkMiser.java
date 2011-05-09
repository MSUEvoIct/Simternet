package simternet.consumer;

import java.util.Collection;

import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * Always use the least expensive edge network available.
 * 
 * @author kkoning
 * 
 */
public class NetworkMiser extends NetManager {

	private static NetworkMiser	singleton;
	private static Double		willSwitchPriceDifference	= 20.0;

	public static NetworkMiser getSingleton() {
		if (NetworkMiser.singleton == null)
			NetworkMiser.singleton = new NetworkMiser();
		return NetworkMiser.singleton;
	}

	@Override
	public void manageNetworks(Consumer c) {
		Collection<Network> availableNetworks = c.s.getNetworks(null, EdgeNetwork.class, c.location);

		// should contain the price and identity of the cheapest network after
		// the for loop below executes.
		double lowestPrice = Double.MAX_VALUE;
		EdgeNetwork lowestPricedNetwork = null;

		// Look at each edge network available at the user's location
		for (Network an : availableNetworks) {
			EdgeNetwork aen = (EdgeNetwork) an;

			// consumption will be for t+1, need to get price for t+1
			double price = aen.getPriceFuture();
			if (price < lowestPrice) {
				lowestPrice = price;
				lowestPricedNetwork = aen;
			}
		}

		// Use a network only if its price is below the maximum the consumer is
		// willing to pay for service at all.
		if (lowestPrice < c.profile.getMaxNetworkPrice()) {
			if (c.edgeNetwork.get() == null)
				c.edgeNetwork.set(lowestPricedNetwork);
			else if (lowestPricedNetwork == c.edgeNetwork.get())
				return; // ready using lowest price network
			else {
				Double baseSwitchProbability = 0.1; // base 10% probability of
				// switching, even if $0.01
				// cheaper
				Double myNetworkPrice = c.edgeNetwork.get().getPriceFuture();
				Double priceDifference = myNetworkPrice - lowestPrice;
				// will be 0 if our current network is the lowest priced, or
				// positive otherwise.
				Double priceSwitchProbability = priceDifference / NetworkMiser.willSwitchPriceDifference;
				if (priceSwitchProbability < 0)
					priceSwitchProbability = 1.1; // cheapest is less than ours,
													// ours went bankrupt!
													// (otherwise it would show
													// up as the cheapest)
				if (priceSwitchProbability >= 1.0)
					c.edgeNetwork.set(lowestPricedNetwork);
				else {
					Double switchProbability = baseSwitchProbability + (1 - baseSwitchProbability)
							* priceSwitchProbability;
					if (c.s.random.nextBoolean(switchProbability))
						c.edgeNetwork.set(lowestPricedNetwork);
				}
			}
		} else
			c.edgeNetwork.set(null);

	}
}
