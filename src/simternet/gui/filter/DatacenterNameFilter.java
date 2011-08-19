package simternet.gui.filter;

import simternet.network.BackboneLink;
import simternet.network.Datacenter;
import simternet.network.Network;

/**
 * Filters Datacenters based on whether or not their name is equal to a given
 * name. Just a sample implementation of a filter.
 * 
 * @author graysonwright
 * 
 */
public class DatacenterNameFilter extends SingleFilter<Network, BackboneLink> {

	private String	name;

	/**
	 * @param n
	 *            the name on which to filter Datacenters
	 */
	public DatacenterNameFilter(String n) {
		name = n;
	}

	@Override
	public boolean acceptEdge(BackboneLink edge) {
		return true;
	}

	@Override
	public boolean acceptVertex(Network vertex) {
		if (vertex instanceof Datacenter)
			if (!((Datacenter) vertex).toString().equals(name))
				return false;
		return true;
	}

}
