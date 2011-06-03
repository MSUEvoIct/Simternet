package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.nsp.NetworkProvider;

public class NetworkProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "NSP,Fitness";

	@Override
	public String getLogger() {
		return "NetworkProviderFitness";
	}

	@Override
	public String getSpecificHeaders() {
		return NetworkProviderFitnessReporter.specificHeaders;
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		Simternet s = (Simternet) state;
		for (NetworkProvider nsp : s.getNetworkServiceProviders())
			this.report(nsp.toString() + Reporter.separater + nsp.financials.getNetWorth());
	}

}
