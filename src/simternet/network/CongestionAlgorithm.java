package simternet.network;

import java.util.List;

/**
 * Congestion algorithms are associated with BackboneLink; their role is to
 * limit the set of network flows passing through such a link so that they
 * conform with the simulated physical characteristics of that link.
 * 
 * @author kkoning
 * 
 */
public interface CongestionAlgorithm {
	
	public List<NetFlow> limit(List<NetFlow> flows, BackboneLink bottleneck);

}
