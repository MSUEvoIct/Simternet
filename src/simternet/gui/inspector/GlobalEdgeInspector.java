package simternet.gui.inspector;

import java.util.Collection;

import simternet.Simternet;
import simternet.gui.inspector.property.DoubleProperty;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * An inspector that does not represent a single agent, but rather all Edge
 * Networks in a given simulation
 * 
 * @author graysonwright
 * 
 */
public class GlobalEdgeInspector extends Inspector {

	protected DoubleProperty	averageTransitBandwidth;

	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes an inspector for all Edge Networks in a given simulation
	 * 
	 * @param sim
	 *            the simulation from which to pull the Edge Networks
	 */
	public GlobalEdgeInspector(Simternet sim) {
		this(sim, "Global Edge Inspector");
	}

	/**
	 * Initializes an inspector for all Edge Networks in a given simulation
	 * 
	 * @param sim
	 *            the simulation from which to pull the Edge Networks
	 * @param title
	 *            the title to place on the inspector's window
	 */
	public GlobalEdgeInspector(Simternet sim, String title) {
		super(sim, title);

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
