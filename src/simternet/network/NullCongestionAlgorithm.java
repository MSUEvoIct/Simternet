package simternet.network;

import java.util.Collection;

/**
 * NullCongestionAlgorithm simply lets all traffic pass, regardless of actual
 * resource constraints. E.g., a 56K/t link can transmit 1G/t without congestion
 * (any modifications to the flows).
 * 
 * @author kkoning
 * 
 */
public class NullCongestionAlgorithm implements CongestionAlgorithm {

	@Override
	public Collection<NetFlow> limit(Collection<NetFlow> flows,
			BackboneLink bottleneck) {
		return flows;
	}

}
