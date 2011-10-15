package simternet.agents.consumer.behavior;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.AppBenefitCalculator;
import simternet.agents.consumer.Consumer;
import simternet.agents.consumer.NetManager;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * RationalNetManager calculates the sum of expected benefits, X from
 * application providers it is currently using on each potential edge network.
 * It then calculates a benefit density for that Edge network by dividing X by
 * the price of that Edge Network. It then consumes the Edge Network with the
 * highest benefit density.
 * 
 * If the consumer is not currently using any applications, choose the cheapest
 * EdgeNetwork.
 * 
 * @author kkoning
 * 
 */
public class RationalNetManager extends NetManager implements Serializable {

	private static RationalNetManager	singleton;

	public static RationalNetManager getSingleton() {
		if (RationalNetManager.singleton == null) {
			RationalNetManager.singleton = new RationalNetManager();
		}
		return RationalNetManager.singleton;
	}

	@Override
	public void manageNetworks(Consumer c) {
		Collection<Network> availableEdges = c.s.getNetworks(null, EdgeNetwork.class, c.getLocation());
		Collection<Consumer.EdgeNetworkBenefit> edgeBenefits = new ArrayList<Consumer.EdgeNetworkBenefit>();

		/*
		 * Populate/calculate the benefits and costs of each edge
		 */
		for (Network net : availableEdges) {
			EdgeNetwork en = (EdgeNetwork) net;
			Consumer.EdgeNetworkBenefit estimatedBenefit = new Consumer.EdgeNetworkBenefit();
			estimatedBenefit.network = en;

			// Aggregate the benefit from the applications.
			for (List<ApplicationProvider> categoryASPs : c.getAppsUsed().values()) {
				for (ApplicationProvider asp : categoryASPs) {
					AppBenefitCalculator abc = c.getAppBenefitCalculator();
					// The % bandwidth we think will get through

					double expectedBenefit = abc.estimateBenefit(c, asp, en);
					estimatedBenefit.sumAppBenefits += expectedBenefit;
				}
			}
			/*
			 * add 1 to edgeBenefits; shouldn't bias calc but still allows
			 * consumer to select the cheapest Edge Network rather than dividing
			 * zero by the price.
			 */
			estimatedBenefit.sumAppBenefits++;

			// If this is the NSP Edge that we're currently using, give it a
			// random propotional bonus to
			// de-synchronize switching behavior.
			EdgeNetwork currentEdge = c.getEdgeNetwork().get();
			if (currentEdge != null) {
				if (c.equals(en)) {
					double randomBonus = c.s.config.networkUsageBonusRatio * c.s.random.nextDouble();
					estimatedBenefit.sumAppBenefits *= 1 + randomBonus;
				}
			}

			// estimatedBenefit now has the sum of all ASPs
			edgeBenefits.add(estimatedBenefit);
		}

		// Find the edge with the highest density that isn't more than our price
		// limit
		EdgeNetwork edgeToSelect = null;
		double highestPreferenceFactor = 0D;
		for (Consumer.EdgeNetworkBenefit enb : edgeBenefits) {
			double networkBenefitExponent = c.s.config.networkBenefitExponent;
			double networkCostExponent = c.s.config.networkCostExponent;
			networkBenefitExponent = networkBenefitExponent + c.networkBenefitExponentVariance;
			networkCostExponent = networkCostExponent + c.networkCostExponentVariance;
			double adjustedBenefit = Math.pow(enb.sumAppBenefits, networkBenefitExponent);
			double adjustedCost = Math.pow(enb.network.getPriceFuture(), networkCostExponent);

			double preferenceFactor = adjustedBenefit / adjustedCost;

			// TODO: Add a benefit scaled switching probability?
			// Compare to benefit on network the user is currently subscribed
			// to, if any.
			if (preferenceFactor > highestPreferenceFactor && enb.network.getPriceFuture() <= c.getMaxNetworkPrice()) {
				highestPreferenceFactor = preferenceFactor;
				edgeToSelect = enb.network;
			}
		}

		// Finally! Consume that network.
		c.setEdgeNetwork(edgeToSelect);

	}

}
