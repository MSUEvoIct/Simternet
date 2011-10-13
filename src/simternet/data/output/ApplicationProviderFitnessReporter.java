package simternet.data.output;

import simternet.agents.asp.ApplicationProvider;
import simternet.engine.Simternet;

public class ApplicationProviderFitnessReporter extends Reporter2 {
	private static final long					serialVersionUID	= 1L;

	private static final int					numFields			= 11;
	private static final String[]				headers;
	private static final String					filename			= "data/output/ASPFitness.out.csv";
	private static transient BufferedCSVWriter	csvWriter;

	static {
		ApplicationProviderFitnessReporter.csvWriter = new BufferedCSVWriter(
				ApplicationProviderFitnessReporter.filename);

		headers = new String[ApplicationProviderFitnessReporter.numFields];
		ApplicationProviderFitnessReporter.headers[0] = "ASP";
		ApplicationProviderFitnessReporter.headers[1] = "Fitness";
		ApplicationProviderFitnessReporter.headers[2] = "Bandwidth";
		ApplicationProviderFitnessReporter.headers[3] = "Quality";
		ApplicationProviderFitnessReporter.headers[4] = "NumCustomers";
		ApplicationProviderFitnessReporter.headers[5] = "CapitalAssets";
		ApplicationProviderFitnessReporter.headers[6] = "LiquidAssets";
		ApplicationProviderFitnessReporter.headers[7] = "TotalInvestment";
		ApplicationProviderFitnessReporter.headers[8] = "TotalFinancing";
		ApplicationProviderFitnessReporter.headers[9] = "TotalOperating";
		ApplicationProviderFitnessReporter.headers[10] = "TotalRevenue";
	}

	public ApplicationProviderFitnessReporter(Simternet s) {
		super(ApplicationProviderFitnessReporter.csvWriter, s);
	}

	@Override
	public void report() {
		for (ApplicationProvider asp : s.getASPs()) {
			Object[] values = new Object[ApplicationProviderFitnessReporter.numFields];

			values[0] = asp.getName();
			values[1] = asp.getFinancials().getNetWorth();
			values[2] = asp.getBandwidth();
			values[3] = asp.getQuality();
			values[4] = asp.getCustomers();
			values[5] = asp.getFinancials().getAssetsCapital();
			values[6] = asp.getFinancials().getAssetsLiquid();
			values[7] = asp.getFinancials().getTotalInvestment();
			values[8] = asp.getFinancials().getTotalFinancingCost();
			values[9] = asp.getFinancials().getTotalOperationsCost();
			values[10] = asp.getFinancials().getTotalRevenue();

			report(values);
		}

	}

	@Override
	public String[] getHeaders() {
		return ApplicationProviderFitnessReporter.headers;
	}

}
