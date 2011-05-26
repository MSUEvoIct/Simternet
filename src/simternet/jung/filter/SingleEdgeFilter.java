package simternet.jung.filter;

import sim.util.Int2D;
import simternet.network.BackboneLink;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class SingleEdgeFilter extends SingleFilter<Network, BackboneLink> {

	private Int2D	location;

	public SingleEdgeFilter(Int2D loc) {
		this.location = loc;
	}

	@Override
	public boolean acceptEdge(BackboneLink edge) {
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

		else
			return true;
	}
}
