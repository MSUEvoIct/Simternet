package simternet.gui.inspector;

import simternet.Simternet;
import simternet.gui.inspector.property.DoubleProperty;
import simternet.gui.inspector.property.IntegerProperty;

public class GlobalNSPInspector extends Inspector {

	DoubleProperty				averageCapitalAssets;
	IntegerProperty				numNSPs;

	private static final long	serialVersionUID	= 1L;

	public GlobalNSPInspector(Simternet sim) {
		this(sim, "Global NSP Inspector");
	}

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
