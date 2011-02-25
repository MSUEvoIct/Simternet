package simternet.consumer;

import java.io.Serializable;
import java.util.Collection;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;

/**
 * Uses only the cheapest network connection at his location. Uses all
 * applications on that connection.
 * 
 * @author kkoning
 * 
 */
public class NetworkServiceMiser extends AbstractConsumerClass implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public AbstractEdgeNetwork	myNetwork;

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
		Collection<AbstractNetwork> c = this.s.getNetworks(null, AbstractEdgeNetwork.class, this.location);
		double lowestPrice = Double.MAX_VALUE;
		AbstractEdgeNetwork lowestPricedNetwork = null;
		for (AbstractNetwork an : c) {
			AbstractEdgeNetwork aen = (AbstractEdgeNetwork) an;
			double price = aen.getPrice(this, this.location);
			if (price < lowestPrice) {
				lowestPrice = price;
				lowestPricedNetwork = aen;
			}
		}
		this.myNetwork = lowestPricedNetwork;
	}

	@Override
	public Boolean usesNetwork(AbstractEdgeNetwork network) {
		return network.equals(this.myNetwork);
	}

}
