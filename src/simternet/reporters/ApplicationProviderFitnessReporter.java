package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.application.ApplicationProvider;

public class ApplicationProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "ASP,Fitness";

	@Override
	public String getLogger() {
		return "ApplicationProviderFitness";
	}

	@Override
	public String getSpecificHeaders() {
		return ApplicationProviderFitnessReporter.specificHeaders;
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		Simternet s = (Simternet) state;
		for (ApplicationProvider asp : s.getASPs())
			this.report(asp.toString() + Reporter.separater + asp.getFinancials().getNetWorth());
	}

}
