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
	public static final String					specificHeaders		= "LocactionX,LocationY,NSP,NetworkType,Price,Customers,Competitors";

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
				this.report(location.x + Reporter.separater + location.y + Reporter.separater + en.getOwner().getName()
						+ Reporter.separater + EdgeDataReporter.netTypeAbbreviations.get(en.getClass().toString())
						+ Reporter.separater + en.getPrice() + Reporter.separater + en.getNumSubscribers()
						+ Reporter.separater + s.getNumNetworkProviders(location));
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
