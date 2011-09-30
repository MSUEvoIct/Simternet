package simternet.data.output;

import java.util.Collection;
import java.util.HashMap;

import sim.engine.SimState;
import sim.util.Int2D;
import simternet.engine.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class EdgeDataReporter extends Reporter {

	protected static HashMap<String, String>	netTypeAbbreviations;
	private static final long					serialVersionUID	= 1L;
	public static final String					specificHeaders;
	static {

		// pos 1 & 2
		specificHeaders = "LocationX" + Reporter.separater + "LocationY" + Reporter.separater + "NSP" // pos
																										// 3
				+ Reporter.separater + "NetworkType" // pos 4
				+ Reporter.separater + "TransitBandwidth" // pos 5
				+ Reporter.separater + "LocalBandwidth" // pos 6
				+ Reporter.separater + "Congestion" // pos 7
				+ Reporter.separater + "Price" // pos 8
				+ Reporter.separater + "Customers" // pos 9
				+ Reporter.separater + "Competitors" // pos 10
				+ Reporter.separater + "MarketShare" // pos 11
				+ Reporter.separater + "Total Usage"; // pos 12

		new EdgeDataReporter().logHeaders();

		// Initialize the hash table for abbreviations of network types.
		EdgeDataReporter.netTypeAbbreviations = new HashMap<String, String>();
		EdgeDataReporter.netTypeAbbreviations.put("class simternet.network.SimpleEdgeNetwork", "Simple");
	}

	public EdgeDataReporter() {
		super();
	}

	public EdgeDataReporter(Integer interval) {
		super(interval);
	}

	@Override
	public void collectData(SimState state) {

		Simternet s = (Simternet) state;
		for (Int2D location : s.allLocations()) {
			Collection<Network> edgeNets = s.getNetworks(null, EdgeNetwork.class, location);
			for (Network edgeNet : edgeNets) {
				EdgeNetwork en = (EdgeNetwork) edgeNet;
				StringBuffer report = new StringBuffer();

				// Hack for reporting odd prices as NA
				double price = en.getPrice();
				if (price > s.config.consumerMaxPriceNSP) {
					price = Double.NaN;
				}

				report.append(location.x); // pos 1
				report.append(Reporter.separater);
				report.append(location.y); // pos 2
				report.append(Reporter.separater);
				report.append(en.getOwner().getName()); // pos 3
				report.append(Reporter.separater);
				// pos 4
				report.append(EdgeDataReporter.netTypeAbbreviations.get(en.getClass().toString()));
				report.append(Reporter.separater);
				report.append(en.getUpstreamIngress().getBandwidth()); // pos 5
				report.append(Reporter.separater);
				report.append(en.getMaxBandwidth()); // pos 6
				report.append(Reporter.separater);
				// pos 7
				report.append(en.getUpstreamIngress().totalCongestionRatio());
				report.append(Reporter.separater);
				report.append(price); // pos 8
				report.append(Reporter.separater);
				report.append(en.getNumSubscribers()); // pos 9
				report.append(Reporter.separater);
				report.append(s.getNumNetworkProviders(location)); // pos 10
				report.append(Reporter.separater);
				// pos 11
				report.append(en.getNumSubscribers() / s.getPopulation(location));
				report.append(Reporter.separater);
				report.append(en.totalUsage); // pos 12
				report(report.toString());
			}
		}

	}

	@Override
	public String getLogger() {
		return "EdgeData";
	}

	@Override
	public String getSpecificHeaders() {
		return EdgeDataReporter.specificHeaders;
	}

}
