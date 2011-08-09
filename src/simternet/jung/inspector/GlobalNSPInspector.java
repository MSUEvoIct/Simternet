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

		this.numNSPs = new IntegerProperty("Number of NSPs");
		this.add(this.numNSPs);

		this.update();
	}

	@Override
	public void update() {
		NetworkProvider[] nsps = (NetworkProvider[]) GUI.getSimternet().getNetworkServiceProviders().toArray();

		this.numNSPs.setValue(nsps.length);

	}
}
