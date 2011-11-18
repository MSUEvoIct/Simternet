package simternet.data.output;

import java.util.Collection;

import sim.util.Int2D;
import simternet.engine.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class EdgeDataReporter extends Reporter {

	// protected static HashMap<String, String> netTypeAbbreviations;
	private static final long		serialVersionUID	= 1L;

	private static final int		numFields			= 11;
	private static final String[]	headers;
	private static final String		filename			= "EdgeData";

	static {
		headers = new String[EdgeDataReporter.numFields];

		EdgeDataReporter.headers[0] = "LocationX";
		EdgeDataReporter.headers[1] = "LocationY";
		EdgeDataReporter.headers[2] = "NSP";
		EdgeDataReporter.headers[3] = "TransitBandwidth";
		EdgeDataReporter.headers[4] = "LocalBandwidth";
		EdgeDataReporter.headers[5] = "Congestion";
		EdgeDataReporter.headers[6] = "Price";
		EdgeDataReporter.headers[7] = "Customers";
		EdgeDataReporter.headers[8] = "Competitors";
		EdgeDataReporter.headers[9] = "MarketShare";
		EdgeDataReporter.headers[10] = "TotalUsage";

		// Initialize the hash table for abbreviations of network types.
		// EdgeDataReporter.netTypeAbbreviations = new HashMap<String,
		// String>();
		// EdgeDataReporter.netTypeAbbreviations.put("class simternet.network.SimpleEdgeNetwork",
		// "Simple");
	}

	public EdgeDataReporter(Simternet s) {
		super(s);
	}

	@Override
	public void report() {
		for (Int2D location : s.allLocations()) {
			Collection<Network> edgeNets = s.getNetworks(null, EdgeNetwork.class, location);
			for (Network edgeNet : edgeNets) {
				EdgeNetwork en = (EdgeNetwork) edgeNet;

				// Hack for reporting odd prices as NA
				// double price = en.getPrice();
				// if (price > s.config.consumerMaxPriceNSP) {
				// price = Double.NaN;
				// }

				Object[] values = new Object[EdgeDataReporter.numFields];
				values[0] = location.x;
				values[1] = location.y;
				values[2] = en.getOwner().getName();
				values[3] = en.getUpstreamIngress().getBandwidth();
				values[4] = en.getMaxBandwidth();
				values[5] = en.getUpstreamIngress().totalCongestionRatio();
				values[6] = en.getPrice();
				values[7] = en.getNumSubscribers();
				values[8] = s.getNumNetworkProviders(location);
				values[9] = en.getNumSubscribers() / s.getNumActiveNSPUsers(location);
				values[10] = en.totalUsage;

				report(values);

				// Grayson's abbreviation for Network Types -kk
				// report.append(EdgeDataReporter.netTypeAbbreviations.get(en.getClass().toString()));
			}
		}

	}

	@Override
	public String[] getHeaders() {
		return EdgeDataReporter.headers;
	}

	@Override
	public String getFileName() {
		return EdgeDataReporter.filename;
	}

}
