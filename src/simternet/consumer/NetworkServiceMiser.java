package simternet.consumer;

import java.io.Serializable;
import java.util.Collection;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * Uses only the cheapest network connection at his location. Uses all
 * applications on that connection.
 * 
 * @author kkoning
 * 
 */
public class NetworkServiceMiser extends AbstractConsumerClass implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public EdgeNetwork	myNetwork;

	public NetworkServiceMiser(Simternet s, Int2D location, Double population, ConsumerProfile profile) {
		super(s, location, population, profile);
	}

	@Override
	protected void consumeApplications() {

		if (this.myNetwork == null)
			return;

		Collection<ApplicationServiceProvider> asps = this.s.getASPs();

		for (ApplicationServiceProvider asp : asps)
			this.consumeApplication(asp, this.myNetwork);

	}

	@Override
	protected void consumeNetworks() {
		if (this.myNetwork == null)
			return;
		this.consumeNetwork(this.myNetwork);
	}

	@Override
	protected void manageNetworks() {
		Collection<Network> c = this.s.getNetworks(null, EdgeNetwork.class, this.location);
		double lowestPrice = Double.MAX_VALUE;
		EdgeNetwork lowestPricedNetwork = null;
		for (Network an : c) {
			EdgeNetwork aen = (EdgeNetwork) an;
			double price = aen.getPrice();
			if (price < lowestPrice) {
				lowestPrice = price;
				lowestPricedNetwork = aen;
			}
		}
		this.myNetwork = lowestPricedNetwork;
	}

	@Override
	public Boolean usesNetwork(EdgeNetwork network) {
		return network.equals(this.myNetwork);
	}

}
