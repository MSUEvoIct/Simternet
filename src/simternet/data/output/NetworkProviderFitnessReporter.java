package simternet.data.output;

import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;

public class NetworkProviderFitnessReporter extends Reporter2 {

	private static final long					serialVersionUID	= 1L;
	public static final String					specificHeaders		= "NSP" + Reporter.separater + "Fitness"
																			+ Reporter.separater + "CapitalAssets"
																			+ Reporter.separater + "LiquidAssets"
																			+ Reporter.separater + "TotalInvestment"
																			+ Reporter.separater + "TotalFinancing"
																			+ Reporter.separater + "TotalOperating"
																			+ Reporter.separater + "TotalRevenue"
																			+ Reporter.separater + "NumEdges"
																			+ Reporter.separater + "Bankrupt"
																			+ Reporter.separater + "NumCustomers"
																			+ Reporter.separater + "MarketShare";

	private static final int					numFields			= 12;
	private static final String[]				headers;
	private static final String					filename			= "data/output/NSPFitness.out.csv";
	private static transient BufferedCSVWriter	csvWriter;

	static {
		NetworkProviderFitnessReporter.csvWriter = new BufferedCSVWriter(NetworkProviderFitnessReporter.filename);

		headers = new String[NetworkProviderFitnessReporter.numFields];
		NetworkProviderFitnessReporter.headers[0] = "NSP";
		NetworkProviderFitnessReporter.headers[1] = "Fitness";
		NetworkProviderFitnessReporter.headers[2] = "CapitalAssets";
		NetworkProviderFitnessReporter.headers[3] = "LiquidAssets";
		NetworkProviderFitnessReporter.headers[4] = "TotalInvestment";
		NetworkProviderFitnessReporter.headers[5] = "TotalFinancing";
		NetworkProviderFitnessReporter.headers[6] = "TotalOperating";
		NetworkProviderFitnessReporter.headers[7] = "TotalRevenue";
		NetworkProviderFitnessReporter.headers[8] = "NumEdges";
		NetworkProviderFitnessReporter.headers[9] = "Bankrupt";
		NetworkProviderFitnessReporter.headers[10] = "NumCustomers";
		NetworkProviderFitnessReporter.headers[11] = "MarketShare";

	}

	public NetworkProviderFitnessReporter(Simternet s) {
		super(NetworkProviderFitnessReporter.csvWriter, s);
	}

	@Override
	public void report() {
		for (NetworkProvider nsp : s.getNetworkServiceProviders()) {
			Double reportedFitness = nsp.financials.getNetWorth();
			if (reportedFitness < -100000) {
				reportedFitness = -100000.0;
			}
			Object[] values = new Object[NetworkProviderFitnessReporter.numFields];

			values[0] = nsp.getName();
			values[1] = reportedFitness;
			values[2] = nsp.financials.getAssetsCapital();
			values[3] = nsp.financials.getAssetsLiquid();
			values[4] = nsp.financials.getTotalInvestment();
			values[5] = nsp.financials.getTotalFinancingCost();
			values[6] = nsp.financials.getTotalOperationsCost();
			values[7] = nsp.financials.getTotalRevenue();
			values[8] = nsp.getEdgeNetworks().size();
			values[9] = nsp.bankrupt;
			values[10] = nsp.getCustomers();
			values[11] = nsp.getCustomers() / s.getPopulation();

			report(values);
		}

	}

	@Override
	public String[] getHeaders() {
		return NetworkProviderFitnessReporter.headers;
	}

}
