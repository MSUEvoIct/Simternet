package simternet.reporters;

import java.util.Collection;

import sim.engine.SimState;
import sim.util.Int2D;
import simternet.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class EdgeDataReporter extends Reporter {
	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "LocactionX,LocationY,NSP,NetworkType,Price,Customers";

	@Override
	public String getLogger() {
		return "EdgeDataReporter";
	}

	@Override
	public String getSpecificHeaders() {
		return EdgeDataReporter.specificHeaders;
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		Simternet s = (Simternet) state;
		for (Int2D location : s.allLocations()) {
			Collection<Network> edgeNets = s.getNetworks(null, EdgeNetwork.class, location);
			for (Network edgeNet : edgeNets) {
				EdgeNetwork en = (EdgeNetwork) edgeNet;
				this.report(location.x + Reporter.separater + location.y + Reporter.separater + en.getOwner()
						+ Reporter.separater + en.getClass() + Reporter.separater + en.getPrice() + Reporter.separater
						+ en.getNumSubscribers());
			}
		}

	}
}
