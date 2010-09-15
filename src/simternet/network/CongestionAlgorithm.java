package simternet.network;

import java.util.Collection;

public interface CongestionAlgorithm {
	public Collection<NetFlow> limit(Collection<NetFlow> flows,
			BackboneLink bottleneck);
}
