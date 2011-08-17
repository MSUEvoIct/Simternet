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

	public SingleEdgeFilter(Int2D loc) {
		this.location = loc;
	}

	@Override
	public boolean acceptEdge(BackboneLink edge) {
		// We don't care about the BackboneLinks.
		return true;
	}

	@Override
	public boolean acceptVertex(Network vertex) {
		if (vertex instanceof EdgeNetwork) {
			if (((EdgeNetwork) vertex).getLocation().equals(this.location))
				return true;
			else
				return false;
		}

		// We don't care about Networks that aren't EdgeNetworks.
		else
			return true;
	}
}
