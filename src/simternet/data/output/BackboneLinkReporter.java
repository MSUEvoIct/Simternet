package simternet.data.output;

import java.util.Collection;

import sim.engine.SimState;
import simternet.agents.asp.ApplicationProvider;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;
import simternet.network.Backbone;
import simternet.network.BackboneLink;
import simternet.network.Datacenter;
import simternet.network.EdgeNetwork;

public class BackboneLinkReporter extends Reporter {
	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders;

	static {
		specificHeaders = "Source" + Reporter.separater // pos 1
				+ "Destination" + Reporter.separater // pos 2
				+ "PerStepDemand" + Reporter.separater // pos 3
				+ "PerStepTransmitted" + Reporter.separater // pos 4
				+ "TotalDemand" + Reporter.separater // pos 5
				+ "TotalTransmitted" + Reporter.separater // pos 6
				+ "Total Capacity"; // pos 7

		new BackboneLinkReporter().logHeaders();
	}

	public BackboneLinkReporter() {
		super();
	}

	public BackboneLinkReporter(Integer interval) {
		super(interval);
	}

	@Override
	public void collectData(SimState state) {

		Simternet s = (Simternet) state;

		// Collect all ASP links to Network Provider backbones
		Collection<ApplicationProvider> asps = s.getASPs();
		for (ApplicationProvider asp : asps) {
			Datacenter dc = asp.getDatacenter();
			Collection<BackboneLink> bbs = dc.egressLinks.values();
			for (BackboneLink bb : bbs) {
				StringBuffer report = new StringBuffer();
				report.append(asp.getName()); // pos 1
				report.append(Reporter.separater);
				report.append(bb.getDestination().toString()); // pos2
				report.append(Reporter.separater);
				report.append(bb.perStepDemand); // pos 3
				report.append(Reporter.separater);
				report.append(bb.perStepTransmitted); // pos 4
				report.append(Reporter.separater);
				report.append(bb.totalDemand); // pos 5
				report.append(Reporter.separater);
				report.append(bb.totalTransmitted); // pos 6
				report.append(Reporter.separater);
				report.append(bb.totalCapacity); // pos 7
				report(report.toString());
			}
		}

		// Collect all NSP backbone links to edge networks
		Collection<NetworkProvider> nsps = s.getNetworkServiceProviders();
		for (NetworkProvider nsp : nsps) {
			Backbone bbn = nsp.getBackboneNetwork();
			Collection<BackboneLink> bbs = bbn.egressLinks.values();
			for (BackboneLink bb : bbs) {
				EdgeNetwork dest = (EdgeNetwork) bb.getDestination();
				String edgeLoc = "Edge@" + dest.getLocation().x + "-" + dest.getLocation().y;
				StringBuffer report = new StringBuffer();

				report.append(nsp.getName()); // pos 1
				report.append(Reporter.separater);
				report.append(edgeLoc); // pos2
				report.append(Reporter.separater);
				report.append(bb.perStepDemand); // pos 3
				report.append(Reporter.separater);
				report.append(bb.perStepTransmitted); // pos 4
				report.append(Reporter.separater);
				report.append(bb.totalDemand); // pos 5
				report.append(Reporter.separater);
				report.append(bb.totalTransmitted); // pos 6
				report.append(Reporter.separater);
				report.append(bb.totalCapacity); // pos 7
				report(report.toString());
			}
		}

	}

	@Override
	public String getLogger() {
		return "BackboneInfo";
	}

	@Override
	public String getSpecificHeaders() {
		return BackboneLinkReporter.specificHeaders;
	}

}
