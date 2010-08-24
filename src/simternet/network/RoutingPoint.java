package simternet.network;

import sim.engine.SimState;
import sim.util.Int2D;
import simternet.nsp.AbstractNetworkProvider;
import simternet.temporal.TemporalHashMap;

public class RoutingPoint extends AbstractNetwork {

	private static final long serialVersionUID = 1L;
	protected TemporalHashMap<AbstractNetwork, Link> routingTable = new TemporalHashMap<AbstractNetwork, Link>();

	public RoutingPoint(final AbstractNetworkProvider nsp, final Int2D location) {

	}

	/**
	 * Other networks notify this one of an incoming link, this function creates
	 * an equal/opposite link back towards the connecting network.
	 * 
	 * @param incoming
	 */
	public void createSymmetricLink(Link incoming) {
		Link duplex = Link.symmetricLink(incoming);
		// for now, just add the link... don't do any checking.
		this.routingTable.put(duplex.destination, duplex);
	}

	@Override
	public Double getBuildCost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
