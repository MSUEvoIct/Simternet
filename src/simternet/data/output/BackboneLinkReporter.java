package simternet.data.output;

import java.util.Collection;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;
import simternet.network.Backbone;
import simternet.network.BackboneLink;
import simternet.network.Datacenter;
import simternet.network.EdgeNetwork;

public class BackboneLinkReporter extends Reporter2 {
	private static final long		serialVersionUID	= 1L;

	public static final int			numFields			= 7;
	public static final String[]	headers;
	public static final String		filename			= "BackboneInfo";

	static {
		headers = new String[BackboneLinkReporter.numFields];

		BackboneLinkReporter.headers[0] = "Source";
		BackboneLinkReporter.headers[1] = "Destination";
		BackboneLinkReporter.headers[2] = "PerStepDemand";
		BackboneLinkReporter.headers[3] = "PerStepTransmitted";
		BackboneLinkReporter.headers[4] = "TotalDemand";
		BackboneLinkReporter.headers[5] = "TotalTransmitted";
		BackboneLinkReporter.headers[6] = "TotalCapacity";
	}

	public BackboneLinkReporter(Simternet s) {
		super(s);
	}

	@Override
	public void report() {
		// TODO Auto-generated method stub
		// Collect all ASP links to Network Provider backbones
		Collection<ApplicationProvider> asps = s.getASPs();
		for (ApplicationProvider asp : asps) {
			Datacenter dc = asp.getDatacenter();
			Collection<BackboneLink> bbs = dc.egressLinks.values();
			for (BackboneLink bb : bbs) {
				Object values[] = new Object[BackboneLinkReporter.numFields];
				values[0] = asp.getName();
				values[1] = bb.getDestination().toString();
				values[2] = bb.perStepDemand;
				values[3] = bb.perStepTransmitted;
				values[4] = bb.totalDemand;
				values[5] = bb.totalTransmitted;
				values[6] = bb.totalCapacity;
				report(values);
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

				Object values[] = new Object[BackboneLinkReporter.numFields];
				values[0] = nsp.getName();
				values[1] = edgeLoc;
				values[2] = bb.perStepDemand;
				values[3] = bb.perStepTransmitted;
				values[4] = bb.totalDemand;
				values[5] = bb.totalTransmitted;
				values[6] = bb.totalCapacity;
				report(values);
			}
		}

	}

	@Override
	public String[] getHeaders() {
		return BackboneLinkReporter.headers;
	}

	@Override
	public String getFileName() {
		return BackboneLinkReporter.filename;
	}

}
