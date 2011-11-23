package simternet.data.output;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;
import simternet.network.BackboneLink;

public class ASPInterconnectionReporter extends Reporter {

	private static final long		serialVersionUID	= 1L;

	public static final int			numFields			= 5;
	public static final String[]	headers;
	public static final String		filename			= "NSP-ASP-Interconnection";

	static {
		headers = new String[ASPInterconnectionReporter.numFields];
		ASPInterconnectionReporter.headers[0] = "NSP";
		ASPInterconnectionReporter.headers[1] = "ASP";
		ASPInterconnectionReporter.headers[2] = "Price";
		ASPInterconnectionReporter.headers[3] = "Quantity";
		ASPInterconnectionReporter.headers[4] = "Congestion";
	}

	public ASPInterconnectionReporter(Simternet s) {
		super(s);
	}

	@Override
	public void report() {
		for (NetworkProvider nsp : s.getNetworkServiceProviders()) {
			for (ApplicationProvider asp : s.getASPs()) {
				BackboneLink bl = asp.getDatacenter().getEgressLink(nsp.getBackboneNetwork());
				double bandwidth = 0.0;
				double congestionRatio = 0.0;
				if (bl != null) {
					bandwidth = bl.getBandwidth();
					congestionRatio = bl.perStepCongestionRatio();
				}
				Object values[] = new Object[ASPInterconnectionReporter.numFields];
				values[0] = nsp.getName();
				values[1] = asp.getName();
				values[2] = nsp.getASPTransitPrice(asp);
				values[3] = bandwidth;
				values[4] = congestionRatio;

				report(values);
			}
		}

	}

	@Override
	public String[] getHeaders() {
		return ASPInterconnectionReporter.headers;
	}

	@Override
	public String getFileName() {
		return ASPInterconnectionReporter.filename;
	}

}
