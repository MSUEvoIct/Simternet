package simternet.consumer;

import simternet.network.EdgeNetwork;

public class EdgeNetworkBenefit {
	public double		sumAppBenefits	= 0;
	public EdgeNetwork	network;

	public double density() {
		double density = sumAppBenefits / network.getPriceFuture();
		return density;
	}
}
