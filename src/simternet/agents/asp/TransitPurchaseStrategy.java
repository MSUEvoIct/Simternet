package simternet.agents.asp;

import simternet.agents.nsp.NetworkProvider;

public interface TransitPurchaseStrategy {
	public Double bandwidthToPurchase(NetworkProvider destination, Double price);
}
