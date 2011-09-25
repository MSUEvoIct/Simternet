package simternet.gui.inspector;

import simternet.engine.Simternet;
import simternet.gui.inspector.property.DoubleProperty;
import simternet.gui.inspector.property.IntegerProperty;

/**
 * An inspector that does not represent a single agent, but rather all Network
 * Providers in a given simulation
 * 
 * @author graysonwright
 * 
 */
public class GlobalNSPInspector extends Inspector {

	DoubleProperty				averageCapitalAssets;
	IntegerProperty				numNSPs;

	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes an inspector for all Network Providers in a given simulation
	 * 
	 * @param sim
	 *            the simulation from which to pull the Network Providers
	 */
	public GlobalNSPInspector(Simternet sim) {
		this(sim, "Global NSP Inspector");
	}

	/**
	 * Initializes an inspector for all Network Providers in a given simulation
	 * 
	 * @param sim
	 *            the simulation from which to pull the Network Providers
	 * @param title
	 *            the title to place on the inspector's window
	 */
	public GlobalNSPInspector(Simternet sim, String title) {
		super(sim, title);

		numNSPs = new IntegerProperty("Number of NSPs", sim);
		this.add(numNSPs);

		this.update();
	}

	@Override
	public void update() {
		Object[] nsps = sim.getNetworkServiceProviders().toArray();

		numNSPs.setValue(nsps.length);

	}
}
