package simternet.jung.inspector;

import simternet.jung.gui.GUI;
import simternet.jung.inspector.property.DoubleProperty;
import simternet.jung.inspector.property.IntegerProperty;
import simternet.nsp.NetworkProvider;

public class GlobalNSPInspector extends Inspector {

	DoubleProperty				averageCapitalAssets;
	IntegerProperty				numNSPs;

	private static final long	serialVersionUID	= 1L;

	public GlobalNSPInspector(GUI owner) {
		this("Global NSP Inspector", owner);
	}

	public GlobalNSPInspector(String title, GUI owner) {
		super(title, owner);

		numNSPs = new IntegerProperty("Number of NSPs", sim);
		this.add(numNSPs);

		this.update();
	}

	@Override
	public void update() {
		NetworkProvider[] nsps = (NetworkProvider[]) sim.getNetworkServiceProviders().toArray();

		numNSPs.setValue(nsps.length);

	}
}
