package simternet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TraceConfig {

	public static final BufferedCSVWriter	output		= new BufferedCSVWriter("trace.log");
	private static final FileWriter			fw;
	private static final BufferedWriter		bw;
	public static final PrintWriter			out;

	static {
		try {
			fw = new FileWriter("trace.log");
			bw = new BufferedWriter(TraceConfig.fw, 1000000); // in bytes
			out = new PrintWriter(TraceConfig.bw);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final boolean				kitchenSink	= false;
	public static final boolean				updates		= false;
	public static final boolean				basics		= false;
	public static final boolean				programFlow	= false;
	public static final boolean				agentInit	= false;

	public static class modelMath {
		public static boolean		aspBenefit	= false;
		public static final boolean	nspBenefit	= false;
	}

	public static class finance {
		public static final boolean	bankruptcy				= false;
		public static final boolean	consumerTransactions	= false;
		public static final boolean	firmTransactions		= false;
		public static final boolean	firmStatus				= false;
		public static final boolean	marketEntry				= false;
	}

	public static class networking {
		public static final boolean	congestionASPSummary	= false;
		public static final boolean	congestionNSPSummary	= false;
		public static final boolean	consumerFlowReceived	= false;
		public static final boolean	aspSentFlow				= false;
		public static final boolean	aspFlowControl			= false;
		public static final boolean	flowCongestion			= false;
		public static final boolean	routingTables			= false;
		public static final boolean	edgeUsageSummary		= false;
		public static final boolean	flowReceived			= false;
		public static final boolean	flowSent				= false;
		public static final boolean	routingDecisions		= false;
		public static final boolean	edgeStatus				= false;
	}

	public static class ops {
		public static final boolean	nspDecisions		= false;
		public static final boolean	aspDecisions		= false;
		public static final boolean	aspTransitDecision	= false;
		public static final boolean	consumerDecisions	= false;
		public static final boolean	nspActions			= false;
		public static final boolean	aspActions			= false;
		public static final boolean	consumerActions		= false;
	}

	public static class summaries {
		public static final boolean	edgeNetworkPrices	= false;
	}

	public static final boolean	consumerPaidNSP			= false;
	public static final boolean	consumerUsedApp			= false;
	public static final boolean	financialStatusASP		= false;
	public static final boolean	financialStatusNSP		= false;
	public static final boolean	NSPBuiltNetwork			= false;
	public static final boolean	NSPCustomerTables		= false;
	public static final boolean	NSPPriceTables			= false;
	public static final boolean	potentialNetworkScoring	= false;
	public static final boolean	steppingASP				= false;
	public static final boolean	steppingConsumer		= false;
	public static final boolean	steppingNSP				= false;
	public static final boolean	sanityChecks			= false;
}
