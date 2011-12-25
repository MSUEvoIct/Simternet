package simternet.network;

import java.util.List;

/**
 * Congestion algorithms are associated with BackboneLink; their role is to
 * limit the set of network flows passing through such a link so that they
 * conform with the simulated physical characteristics of that link.
 * 
 * A Congestion algorithm works on the Simternet equivalent of a router input queue,
 * though, rather than operating on the instant packets, it operates on a set of NetFlow
 * objects that represent aggregated usage over an entire simulation time step.  This allows
 * congestion algorithms to scale with the number of application / user pairs, but not 
 * bandwidth, as processing 1:1 simulated packets would be remarkably inefficient for
 * this type of simulation.
 * 
 * @author kkoning
 * 
 */
public interface CongestionAlgorithm {
	
	/** 
	 * Accepts a list of NetFlows requesting resources, returns a (possibly altered) list 
	 * of NetFlows which have passed through this link.
	 * 
	 * @param flows  A list of flows requesting resources on the specified backbone link.
	 * @param bottleneck  The BackboneLink on which these flows are to be transmitted
	 * @return A list of (possibly) altered NetFlow objects which now conform to the 
	 *  characteristics of this congestion algorithm on the specified backbone link.
	 */
	public List<NetFlow> limit(List<NetFlow> flows, BackboneLink bottleneck);

}
