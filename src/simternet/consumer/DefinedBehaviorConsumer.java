package simternet.consumer;

import sim.util.Int2D;
import simternet.Simternet;

/**
 * In this class we're specifically defining the behavior of consumers, rather
 * than having it be determined by the ouput of an evolutionary algorithm.
 * 
 * @author kkoning
 * 
 */
@Deprecated
public class DefinedBehaviorConsumer extends Consumer {

	private static final long	serialVersionUID	= 1L;

	protected DefinedBehaviorConsumer(Simternet s, Int2D location, Double population, ConsumerProfile profile,
			NetManager netManager, AppManager appManager, AppBenefitCalculator abc) {
		super(s, location, population, profile, netManager, appManager, abc);
	}

	// @Override
	// protected void manageNetworks() {
	//
	// EdgeNetwork currentNetwork = this.subscribedTo.get();
	//
	// Collection<Network> c = this.s.getNetworks(null, EdgeNetwork.class,
	// this.location);
	// double lowestPrice = Double.MAX_VALUE;
	// EdgeNetwork lowestPricedNetwork = null;
	// for (Network an : c) {
	// EdgeNetwork aen = (EdgeNetwork) an;
	// double price = aen.getPriceFuture();
	// if (price < lowestPrice) {
	// lowestPrice = price;
	// lowestPricedNetwork = aen;
	// }
	// }
	//
	// if (lowestPrice < this.profile.getMaxNetworkPrice()) {
	// if (this.subscribedTo.get() == null)
	// this.subscribedTo.set(lowestPricedNetwork);
	// else if (this.s.random.nextBoolean(0.1))
	// this.subscribedTo.set(lowestPricedNetwork);
	// } else
	// this.subscribedTo.set(null);
	//
	// }

}
