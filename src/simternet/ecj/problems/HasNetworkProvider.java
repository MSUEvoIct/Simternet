package simternet.ecj.problems;

import simternet.nsp.NetworkProvider;

public interface HasNetworkProvider extends HasPotentialNetwork {
	public NetworkProvider getNetworkProvider();

}
