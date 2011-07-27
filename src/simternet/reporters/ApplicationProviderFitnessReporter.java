package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.application.ApplicationProvider;

public class ApplicationProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "ASP,Fitness,Bandwidth,Quality,NumCustomers,CapitalAssets,"
															+ "LiquidAssets,TotalInvestment,TotalFinancing,TotalOperating,TotalRevenue";

	static {
		new ApplicationProviderFitnessReporter().logHeaders();
	}

	public ApplicationProviderFitnessReporter() {
		super();
	}

	public ApplicationProviderFitnessReporter(int i) {
		super(i);
	}

	public ApplicationProviderFitnessReporter(Integer interval) {
		super(interval);
	}

	@Override
	public void collectData(SimState state) {
		Simternet s = (Simternet) state;
		for (ApplicationProvider asp : s.getASPs()) {
			StringBuffer report = new StringBuffer();
			report.append(asp.getName());
			report.append(Reporter.separater);
			report.append(asp.getFinancials().getNetWorth());
			report.append(Reporter.separater);
			report.append(asp.getBandwidth());
			report.append(Reporter.separater);
			report.append(asp.getQuality());
			report.append(Reporter.separater);
			report.append(asp.getCustomers());
			report.append(Reporter.separater);
			report.append(asp.getFinancials().getAssetsCapital());
			report.append(Reporter.separater);
			report.append(asp.getFinancials().getAssetsLiquid());
			report.append(Reporter.separater);
			report.append(asp.getFinancials().getTotalInvestment());
			report.append(Reporter.separater);
			report.append(asp.getFinancials().getTotalFinancingCost());
			report.append(Reporter.separater);
			report.append(asp.getFinancials().getTotalOperationsCost());
			report.append(Reporter.separater);
			report.append(asp.getFinancials().getTotalRevenue());

			this.report(report.toString());
		}

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
