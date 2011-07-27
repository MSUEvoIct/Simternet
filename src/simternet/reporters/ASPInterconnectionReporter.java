package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.network.BackboneLink;
import simternet.nsp.NetworkProvider;

public class ASPInterconnectionReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "NSP,Price,ASP,Quantity";

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
