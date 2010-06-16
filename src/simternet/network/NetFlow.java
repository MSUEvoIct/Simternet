package simternet.network;

import simternet.consumer.AbstractConsumerClass;

public class NetFlow {
	public AbstractConsumerClass acc;
	/**
	 * The amount of usage in, TODO: think about units.
	 */
	public Double amount;
	public Double bandwidth;
	public Datacenter datacenter;
	public AbstractNetwork edgeNetwork;

}
