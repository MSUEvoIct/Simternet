package simternet.application;

import simternet.nsp.NetworkProvider;

public interface TransitPurchaseStrategy {
	public Double bandwidthToPurchase(NetworkProvider destination, Double price);
}
