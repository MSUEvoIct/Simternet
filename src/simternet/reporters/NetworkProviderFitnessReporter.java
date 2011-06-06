package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.nsp.NetworkProvider;

public class NetworkProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "NSP,Fitness";
	private Integer				interval			= 1;

	public NetworkProviderFitnessReporter(Integer interval) {
		this.interval = interval;
	}

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
		if ((state.schedule.getSteps() % this.interval) != 0)
			return;
		super.step(state);
		Simternet s = (Simternet) state;
		for (NetworkProvider nsp : s.getNetworkServiceProviders()) {
			Double reportedFitness = nsp.financials.getNetWorth();
			if (reportedFitness < 0)
				reportedFitness = -1000000.0;
			this.report(nsp.toString() + Reporter.separater + reportedFitness);

		}
	}

}
