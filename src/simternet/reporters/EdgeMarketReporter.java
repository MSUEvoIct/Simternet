package simternet.reporters;

import sim.engine.SimState;
import sim.util.Int2D;
import simternet.Simternet;

public class EdgeMarketReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "LocactionX,LocationY,NumNetworks";

	static {
		new EdgeMarketReporter().logHeaders();
	}

	@Override
	public void collectData(SimState state) {
		Simternet s = (Simternet) state;
		for (Int2D location : s.allLocations()) {
			int numNets = s.getNumNetworkProviders(location);
			this.report(location.x + Reporter.separater + location.y + Reporter.separater + numNets);
		}
	}

	@Override
	public String getLogger() {
		return "EdgeMarket";
	}

	@Override
	public String getSpecificHeaders() {
		return EdgeDataReporter.specificHeaders;
	}

}
