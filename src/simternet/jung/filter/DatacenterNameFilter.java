package simternet.jung.filter;

import simternet.network.BackboneLink;
import simternet.network.Datacenter;
import simternet.network.Network;

public class DatacenterNameFilter extends SingleFilter<Network, BackboneLink> {

	private String	name;

	public DatacenterNameFilter(String n) {
		this.name = n;
	}

	@Override
	public boolean acceptEdge(BackboneLink edge) {
		return true;
	}

	@Override
	public boolean acceptVertex(Network vertex) {
		if (vertex instanceof Datacenter)
			if (!((Datacenter) vertex).toString().equals(this.name))
				return false;
		return true;
	}

}
