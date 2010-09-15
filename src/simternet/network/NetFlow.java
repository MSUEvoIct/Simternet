package simternet.network;

import simternet.consumer.AbstractConsumerClass;

public class NetFlow {
	public AbstractConsumerClass user;
	/**
	 * The amount of usage in, TODO: think about units.
	 */
	public Double amount;
	public Double bandwidth;
	public AbstractNetwork destination;
	public Integer hops = 0;
	public Double latency = 0D;
	public AbstractNetwork source;
	public Integer timesRouted = 0;

}
