package simternet.gui.filter;

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

	/**
	 * @param cutoff
	 *            A cutoff value under which we won't display the links
	 */
	public HighPassFilter(double cutoff) {
		this.cutoff = cutoff;
	}

	@Override
	public boolean acceptEdge(BackboneLink edge) {
		if (edge.getBandwidth() >= cutoff)
			return true;
		return false;
	}

	@Override
	public boolean acceptVertex(Network vertex) {
		return true;
	}

}
