package simternet.data.output;

import sim.engine.SimState;
import simternet.agents.asp.ApplicationProvider;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;
import simternet.network.BackboneLink;

public class ASPInterconnectionReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "NSP" + Reporter.separater + 
														"Price" + Reporter.separater + 
														"ASP" + Reporter.separater + 
														"Quantity";

	static {
		new ASPInterconnectionReporter().logHeaders();
	}

	public ASPInterconnectionReporter() {
		super();
	}

	public ASPInterconnectionReporter(int i) {
		super(i);
	}

	@Override
	public void collectData(SimState state) {
		Simternet s = (Simternet) state;
		for (NetworkProvider nsp : s.getNetworkServiceProviders())
			for (ApplicationProvider asp : s.getASPs()) {
				BackboneLink bl = asp.getDatacenter().getEgressLink(nsp.getBackboneNetwork());
				Double bandwidth = 0.0;
				if (bl != null)
					bandwidth = bl.getBandwidth();

				StringBuffer report = new StringBuffer();
				report.append(nsp.getName());
				report.append(Reporter.separater);
				report.append(nsp.getASPTransitPrice(asp));
				report.append(Reporter.separater);
				report.append(asp.getName());
				report.append(Reporter.separater);
				report.append(bandwidth);

				this.report(report.toString());

			}
	}

	@Override
	public String getLogger() {
		return "ASPInterconnection";
	}

	@Override
	public String getSpecificHeaders() {
		return ASPInterconnectionReporter.specificHeaders;
	}

}
