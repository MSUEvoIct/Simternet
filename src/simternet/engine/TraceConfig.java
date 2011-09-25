package simternet.engine;

public class TraceConfig {

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
		public static final boolean	routingTables			= false;
		public static final boolean	edgeUsageSummary		= false;
	}

	public static class ops {
		public static final boolean	nspDecisions		= false;
		public static final boolean	aspDecisions		= false;
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
