package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.application.ApplicationProvider;

public class ApplicationProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;

	public static final String	specificHeaders		= "ASP,Fitness";

	public ApplicationProviderFitnessReporter() {
		super();
	}

	public ApplicationProviderFitnessReporter(int i) {
		super(i);
	}

	@Override
	public void collectData(SimState state) {
		// TODO Auto-generated method stub
		Simternet s = (Simternet) state;
		for (ApplicationProvider asp : s.getASPs())
			this.report(asp.toString() + Reporter.separater + asp.getFinancials().getNetWorth());

	}

	@Override
	public String getLogger() {
		return "ApplicationProviderFitness";
	}

	@Override
	public String getSpecificHeaders() {
		return ApplicationProviderFitnessReporter.specificHeaders;
	}

}
