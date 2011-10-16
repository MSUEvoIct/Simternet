package simternet.data.output;

import sim.util.Int2D;
import simternet.engine.Simternet;

public class EdgeMarketReporter extends Reporter2 {

	private static final long		serialVersionUID	= 1L;

	private static final int		numFields			= 4;
	private static final String[]	headers;
	public static final String		fileName			= "EdgeMarket";

	static {
		headers = new String[EdgeMarketReporter.numFields];
		EdgeMarketReporter.headers[0] = "LocationX";
		EdgeMarketReporter.headers[1] = "LocationY";
		EdgeMarketReporter.headers[2] = "NumConsumers";
		EdgeMarketReporter.headers[3] = "NumNetworks";

	}

	public EdgeMarketReporter(Simternet s) {
		super(s);
	}

	@Override
	public void report() {
		for (Int2D location : s.allLocations()) {
			int numNets = s.getNumNetworkProviders(location);

			Object values[] = new Object[EdgeMarketReporter.numFields];
			values[0] = location.x;
			values[1] = location.y;
			values[2] = s.getPopulation(location).intValue();
			values[3] = numNets;

			report(values);
		}
	}

	@Override
	public String[] getHeaders() {
		return EdgeMarketReporter.headers;
	}

	@Override
	public String getFileName() {
		return EdgeMarketReporter.fileName;
	}

}
