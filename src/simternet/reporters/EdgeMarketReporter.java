package simternet.reporters;

import sim.engine.SimState;
import sim.util.Int2D;
import simternet.Simternet;

public class EdgeMarketReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "LocactionX,LocationY,NumConsumers,NumNetworks";

	static {
		new EdgeMarketReporter().logHeaders();
	}

	public EdgeMarketReporter() {

	}

	public EdgeMarketReporter(int interval) {
		super(interval);
	}

	@Override
	public void collectData(SimState state) {
		Simternet s = (Simternet) state;
		for (Int2D location : s.allLocations()) {
			int numNets = s.getNumNetworkProviders(location);

			StringBuffer sb = new StringBuffer();
			sb.append(location.x);
			sb.append(Reporter.separater);
			sb.append(location.y);
			sb.append(Reporter.separater);
			sb.append(s.getPopulation(location));
			sb.append(Reporter.separater);
			sb.append(numNets);

			this.report(sb.toString());
		}
	}

	@Override
	public String getLogger() {
		return "EdgeMarket";
	}

	@Override
	public String getSpecificHeaders() {
		return EdgeMarketReporter.specificHeaders;
	}

}
