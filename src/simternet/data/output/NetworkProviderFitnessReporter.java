package simternet.data.output;

import sim.engine.SimState;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;

public class NetworkProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= 	"NSP" + Reporter.separater + 
														"Fitness" + Reporter.separater + 
														"CapitalAssets" + Reporter.separater + 
														"LiquidAssets" + Reporter.separater + 
														"TotalInvestment" + Reporter.separater + 
														"TotalFinancing" + Reporter.separater + 
														"TotalOperating" + Reporter.separater + 
														"TotalRevenue" + Reporter.separater + 
														"NumEdges" + Reporter.separater + 
														"Bankrupt" + Reporter.separater + 
														"NumCustomers" + Reporter.separater + 
														"MarketShare";

	static {
		new NetworkProviderFitnessReporter().logHeaders();
	}

	public NetworkProviderFitnessReporter() {
		super();
	}

	public NetworkProviderFitnessReporter(int i) {
		super(i);
	}

	@Override
	public void collectData(SimState state) {
		Simternet s = (Simternet) state;
		for (NetworkProvider nsp : s.getNetworkServiceProviders()) {
			Double reportedFitness = nsp.financials.getNetWorth();
			if (reportedFitness < 0)
				reportedFitness = 0.0;

			StringBuffer report = new StringBuffer();
			report.append(nsp.getName());
			report.append(Reporter.separater);
			report.append(reportedFitness);
			report.append(Reporter.separater);
			report.append(nsp.financials.getAssetsCapital());
			report.append(Reporter.separater);
			report.append(nsp.financials.getAssetsLiquid());
			report.append(Reporter.separater);
			report.append(nsp.financials.getTotalInvestment());
			report.append(Reporter.separater);
			report.append(nsp.financials.getTotalFinancingCost());
			report.append(Reporter.separater);
			report.append(nsp.financials.getTotalOperationsCost());
			report.append(Reporter.separater);
			report.append(nsp.financials.getTotalRevenue());
			report.append(Reporter.separater);
			report.append(nsp.getEdgeNetworks().size());
			report.append(Reporter.separater);
			report.append(nsp.bankrupt);
			report.append(Reporter.separater);
			report.append(nsp.getCustomers());
			report.append(Reporter.separater);
			report.append(nsp.getCustomers() / s.getPopulation());

			this.report(report.toString());

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
