package simternet.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.util.Int2D;
import simternet.network.EdgeNetwork;

/**
 * This class contains all the exogenous variables used as inputs to the model.
 * Due to MASON's integrated checkpointing functionality, the same code may be
 * used to continue running different instances of the simulation, some of which
 * will likely use different values for these parameters. These values must
 * remain with the simulation instance when it is serialized, so that the same
 * values can be used when the simulation is restored from the checkpoint. For
 * example, a new network service provider may try to build its network assuming
 * a 10x10 grid, but the serialized simulation may have been initialized using a
 * 5x5 grid. Obviously this would cause... problems.
 * 
 * The Java Properties class was chosen for ease of use in terms of user editing
 * of simulation properties stored in text files.
 * 
 * @author kkoning
 * 
 */
public class SimternetConfig extends Properties implements Serializable {

	/**
	 * Version specification is necessary to use persisted states across
	 * different versions of the model as it evolves. If this class has changed
	 * substantially, it may no likely longer be possible to resume an old
	 * simulation using current code.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * If an application was congested on an edge application, try at the
	 * observed bandwidth * (1 + networkFlowGrowthProportion).
	 * 
	 * See Datacenter.originate
	 */
	public final double			applicationFlowGrowthProportion;

	/**
	 * Flows may be reduced to 0; if so they will never recover from using
	 * applicationFlowGrowthProportion alone. Alternatively, the minimum
	 * bandwidth to try is the applications's bandwidth *
	 * applicationFlowMinimumProportion
	 */
	public final double			applicationFlowMinimumProportion;

	public final double			aspEndowment;

	public final double			consumerIndifferencePrice;
	public final double			consumerMaxPriceNSP;
	public final double			consumerPopulationMax;
	// TODO: Rename; see RationalNetManager
	public final double			networkBenefitExponent;
	public final double			networkCostExponent;
	public final double			networkValueExponentVarianceRange;

	/**
	 * Used to grant a random bonus to ASPs which were used in the previous time
	 * step, thereby adding a random element to consumer switching behavior in
	 * terms of when the ASP falls far enough in relation to its competitors to
	 * drop out of the set of applications used by the consumer.
	 */
	public final double			applicationUsageBonusRatio;
	public final double			networkUsageBonusRatio;

	public final double			financeDepreciationRate;
	public final double			financeInterestRate;
	public final double			financePaybackRate;

	public final Int2D			gridSize;

	public final double			networkSimpleBuildCostFixed;
	public final double			networkSimpleBuildCostPerUser;
	public final double			networkSimpleOpCostFixed;
	public final double			networkSimpleOpCostPerUser;

	public final double			nspEndowment;

	public final double			nspInitialEdgeNetworkBandwidth;

	public final String			prefixASP;
	public final String			prefixConsumer;
	public final String			prefixNSP;

	public final String			simternetOutputDir;

	public final Simternet		s;

	/**
	 * Initializes model parameters from data/configuration/default.properties
	 */
	public SimternetConfig(Simternet sim) {
		this(sim, "data/config/simternet.properties");
	}

	/**
	 * Initializes model parameters from specified file
	 * 
	 * @param file
	 *            The Java Properties file to load
	 */
	public SimternetConfig(Simternet sim, String file) {

		// Keep a back-reference to this Simternet
		s = sim;

		// Load our config file
		try {
			FileInputStream fis = new FileInputStream(file);
			this.load(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			Logger.getRootLogger().log(Level.FATAL, "Cannot load SimternetConfig", e);
		} catch (IOException e) {
			Logger.getRootLogger().log(Level.FATAL, "Cannot load SimternetConfig", e);
		}

		// Parse variables from config file here.
		applicationFlowGrowthProportion = d("applicationFlowGrowthProportion");
		applicationFlowMinimumProportion = d("applicationFlowMinimumProportion");

		aspEndowment = d("aspEndowment");
		consumerIndifferencePrice = d("consumerIndifferencePrice");
		consumerMaxPriceNSP = d("consumerMaxPriceNSP");
		consumerPopulationMax = d("consumerPopulationMax");

		networkBenefitExponent = d("alpha");
		networkCostExponent = d("gamma");
		networkValueExponentVarianceRange = d("networkValueExponentVarianceRange");

		applicationUsageBonusRatio = d("applicationUsageBonusRatio");
		networkUsageBonusRatio = d("networkUsageBonusRatio");

		financeDepreciationRate = d("financeDepreciationRate");
		financeInterestRate = d("financeInterestRate");
		financePaybackRate = d("financePaybackRate");
		gridSize = new Int2D(i("gridSize.x"), i("gridSize.y"));
		networkSimpleBuildCostFixed = d("networkSimpleBuildCostFixed");
		networkSimpleBuildCostPerUser = d("networkSimpleBuildCostPerUser");
		networkSimpleOpCostFixed = d("networkSimpleOpCostFixed");
		networkSimpleOpCostPerUser = d("networkSimpleOpCostPerUser");

		nspEndowment = d("nspEndowment");

		nspInitialEdgeNetworkBandwidth = d("nspInitialEdgeNetworkBandwidth");

		prefixASP = s("prefixASP");
		prefixConsumer = s("prefixConsumer");
		prefixNSP = s("prefixNSP");
		simternetOutputDir = s("simternetOutputDir");

	}

	//
	// DEFINED EQUATIONS
	//

	public static double edgeBackboneUpgradeCost(EdgeNetwork en, double capacityToAdd) {
		// double currentBandwidth = en.getUpstreamIngress().getBandwidth();
		// Quick-and-dirty scale
		double cost = Math.pow(capacityToAdd / 10E6, 1.5);
		return cost;
	}

	//
	// CONVIENENCE FUNCTIONS
	//

	private final double d(String param) {
		return Double.parseDouble(this.getProperty(param));
	}

	private final int i(String param) {
		return Integer.parseInt(this.getProperty(param));
	}

	private final String s(String param) {
		return this.getProperty(param);
	}

}
