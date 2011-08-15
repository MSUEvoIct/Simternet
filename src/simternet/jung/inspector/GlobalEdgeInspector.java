package simternet.jung.inspector;

import java.util.Collection;

import simternet.jung.gui.GUI;
import simternet.jung.inspector.property.DoubleProperty;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class GlobalEdgeInspector extends Inspector {

	protected DoubleProperty	averageTransitBandwidth;

	private static final long	serialVersionUID	= 1L;

	public GlobalEdgeInspector(GUI gui) {
		this("Global Edge Inspector", gui);
	}

	public GlobalEdgeInspector(String title, GUI owner) {
		super(title, owner);

		averageTransitBandwidth = new DoubleProperty("Average Transit Bandwidth", sim);
		this.add(averageTransitBandwidth);

		this.update();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		double bandwidthTotal = 0;

		Collection<Network> edges = sim.getNetworks(null, EdgeNetwork.class, null);

		for (Network net : edges)
			if (net instanceof EdgeNetwork) {
				bandwidthTotal += ((EdgeNetwork) net).getUpstreamIngress().getBandwidth();
			}

		averageTransitBandwidth.setValue(bandwidthTotal / edges.size());
	}

}
