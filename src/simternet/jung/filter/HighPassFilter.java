package simternet.jung.filter;

import simternet.network.BackboneLink;
import simternet.network.Network;

/**
 * Filters for BackboneLinks that have a bandwidth higher than a given cutoff
 * value
 * 
 * @author graysonwright
 * 
 */
public class HighPassFilter extends SingleFilter<Network, BackboneLink> {

	private double	cutoff;

	public HighPassFilter(double cutoff) {
		this.cutoff = cutoff;
	}

	@Override
	public boolean acceptEdge(BackboneLink edge) {
		if (edge.getBandwidth() >= this.cutoff)
			return true;
		return false;
	}

	@Override
	public boolean acceptVertex(Network vertex) {
		return true;
	}

}
