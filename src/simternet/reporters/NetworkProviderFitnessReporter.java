package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.nsp.NetworkProvider;

public class NetworkProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "NSP,Fitness";

	public NetworkProviderFitnessReporter(int i) {
		super(i);
	}

	@Override
	public void collectData(SimState state) {
		// TODO Auto-generated method stub
		Simternet s = (Simternet) state;
		for (NetworkProvider nsp : s.getNetworkServiceProviders()) {
			Double reportedFitness = nsp.financials.getNetWorth();
			if (reportedFitness < 0)
				reportedFitness = -1000000.0;
			this.report(nsp.toString() + Reporter.separater + reportedFitness);

		}
	}

	@Override
	public String getLogger() {
		return "NetworkProviderFitness";
	}

	@Override
	public String getSpecificHeaders() {
		return NetworkProviderFitnessReporter.specificHeaders;
	}

}
