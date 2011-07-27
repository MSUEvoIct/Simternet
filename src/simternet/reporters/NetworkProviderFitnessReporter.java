package simternet.reporters;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.nsp.NetworkProvider;

public class NetworkProviderFitnessReporter extends Reporter {

	private static final long	serialVersionUID	= 1L;
	public static final String	specificHeaders		= "NSP,Fitness,CapitalAssets,LiquidAssets,TotalInvestment,TotalFinancing,TotalOperating,TotalRevenue,NumEdges,Bankrupt";

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
