package simternet.network;

import java.util.List;

/**
 * NullCongestionAlgorithm simply lets all traffic pass, regardless of actual
 * resource constraints. E.g., a 56K/t link can transmit 1G/t without congestion
 * (any modifications to the flows).
 * 
 * @author kkoning
 * 
 */
public class NullCongestionAlgorithm implements CongestionAlgorithm {

	protected BackboneLink	link;

	public NullCongestionAlgorithm(BackboneLink link) {
		this.link = link;
	}

	/*
	 * Always report that we have zero congestion
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.network.CongestionAlgorithm#getCongestionRatio()
	 */
	@Override
	public Double getCongestionRatio() {
		return 0.0;
	}

	@Override
	public String getCongestionReport() {
		return "Ignored";
	}

	@Override
	public BackboneLink getLink() {
		return this.link;
	}

	@Override
	public List<NetFlow> limit(List<NetFlow> flows, BackboneLink bottleneck) {
		return flows;
	}

}
