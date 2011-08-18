package simternet.jung;

import java.util.Collection;
import java.util.HashMap;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.nsp.NetworkProvider;

/**
 * A network object that represents the consumers at a given geographic
 * location, from which consumers can connect to edge networks at that location.
 * Only one instance per location per simternet. Introduced for visualization
 * purposes.
 * 
 * @author graysonwright
 */
public class ConsumerNetwork extends Network {

	protected Int2D												location;
	public Simternet											s;

	static HashMap<Simternet, HashMap<Int2D, ConsumerNetwork>>	networkMap			= new HashMap<Simternet, HashMap<Int2D, ConsumerNetwork>>();

	private static final long									serialVersionUID	= 1L;

	public static ConsumerNetwork get(EdgeNetwork edge) {
		return ConsumerNetwork.get(edge.getOwner().s, edge.getLocation());
	}

	/**
	 * Returns a reference to the shared ConsumerNetwork instance for a given
	 * Simternet and location
	 * 
	 * @param simternet
	 * @param location
	 * @return the locationNetwork in the given Simternet simulation at the
	 *         given location
	 */
	public static ConsumerNetwork get(Simternet simternet, Int2D location) {

		if (!ConsumerNetwork.networkMap.containsKey(simternet)) {
			ConsumerNetwork.networkMap.put(simternet, new HashMap<Int2D, ConsumerNetwork>());
		}

		HashMap<Int2D, ConsumerNetwork> locationMap = ConsumerNetwork.networkMap.get(simternet);

		if (!locationMap.containsKey(location)) {
			locationMap.put(location, new ConsumerNetwork(simternet, location));
		}

		return locationMap.get(location);
	}

	protected ConsumerNetwork(Simternet sim, Int2D location) {
		s = sim;
		this.location = location;
	}

	public Double getActiveSubscribers() {
		return s.getAllActiveSubscribersGrid().get(location.x, location.y);
	}

	public Double getActiveSubscribers(NetworkProvider np) {
		return s.getMyActiveSubscribersGrid(np).get(location.x, location.y);
	}

	public Int2D getLocation() {
		return location;
	}

	public Collection<Network> getNetworks() {
		return s.getNetworks(location);
	}

	public Collection<Network> getNetworks(NetworkProvider nsp, Class<? extends Network> netType) {
		return s.getNetworks(nsp, netType, location);
	}

	public Integer getNumNetworkProviders() {
		return s.getNumNetworkProviders(location);
	}

	public Double getPercentageSubscribing() {
		return new Double(this.getActiveSubscribers() / getPopulation());
	}

	public Double getPopulation() {
		return s.getPopulation(location);
	}

}
