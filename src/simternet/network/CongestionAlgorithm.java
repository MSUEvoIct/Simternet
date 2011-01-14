package simternet.network;

import java.util.List;

public interface CongestionAlgorithm {
	public String getCongestionReport();

	public BackboneLink getLink();

	public List<NetFlow> limit(List<NetFlow> flows, BackboneLink bottleneck);
}
