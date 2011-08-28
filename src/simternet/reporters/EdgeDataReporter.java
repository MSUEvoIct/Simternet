package simternet.reporters;

import java.util.Collection;
import java.util.HashMap;

import sim.engine.SimState;
import sim.util.Int2D;
import simternet.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class EdgeDataReporter extends Reporter {

	protected static HashMap<String, String>	netTypeAbbreviations;
	private static final long					serialVersionUID	= 1L;
	public static final String					specificHeaders		= "LocationX" + Reporter.separater + "LocationY"
																			+ Reporter.separater + "NSP"
																			+ Reporter.separater + "NetworkType"
																			+ Reporter.separater + "TransitBandwidth"
																			+ Reporter.separater + "LocalBandwidth"
																			+ Reporter.separater + "Congestion"
																			+ Reporter.separater + "Price"
																			+ Reporter.separater + "Customers"
																			+ Reporter.separater + "Competitors"
																			+ Reporter.separater + "MarketShare"
																			+ Reporter.separater + "Total Usage";

	static {
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

				report.append(location.x);
				report.append(Reporter.separater);
				report.append(location.y);
				report.append(Reporter.separater);
				report.append(en.getOwner().getName());
				report.append(Reporter.separater);
				report.append(EdgeDataReporter.netTypeAbbreviations.get(en.getClass().toString()));
				report.append(Reporter.separater);
				report.append(en.getUpstreamIngress().getBandwidth());
				report.append(Reporter.separater);
				report.append(en.getMaxBandwidth());
				report.append(Reporter.separater);
				report.append(en.getUpstreamIngress().getCongestionAlgorithm().getUsageRatio());
				report.append(Reporter.separater);
				report.append(price);
				report.append(Reporter.separater);
				report.append(en.getNumSubscribers());
				report.append(Reporter.separater);
				report.append(s.getNumNetworkProviders(location));
				report.append(Reporter.separater);
				report.append(en.getNumSubscribers() / s.getPopulation(location));
				report.append(Reporter.separater);
				report.append(en.totalUsage);
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
