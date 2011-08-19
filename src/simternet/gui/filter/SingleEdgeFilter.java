package simternet.gui.filter;

import sim.util.Int2D;
import simternet.network.BackboneLink;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * Given a location (Int2D), rejects all edgeNetworks that are not at that
 * location.
 * 
 * @author graysonwright
 * 
 */
public class SingleEdgeFilter extends SingleFilter<Network, BackboneLink> {

	// Location to accept
	private Int2D	location;

	/**
	 * @param loc
	 *            the location that we're filtering for
	 */
	public SingleEdgeFilter(Int2D loc) {
		location = loc;
	}

	@Override
	public boolean acceptEdge(BackboneLink edge) {
		// We don't care about the BackboneLinks.
		return true;
	}

	@Override
	public boolean acceptVertex(Network vertex) {
		// If it's an edgenetwork, then we only want the one.
		// if it's not an edgenetwork, we don't care. so we'll take it.
		if (vertex instanceof EdgeNetwork)
			return ((EdgeNetwork) vertex).getLocation().equals(location);
		else
			return true;
	}
}
