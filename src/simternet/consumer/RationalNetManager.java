package simternet.consumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import simternet.application.ApplicationProvider;
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
		Collection<Network> availableEdges = c.s.getNetworks(null, EdgeNetwork.class, c.location);
		Collection<EdgeNetworkBenefit> edgeBenefits = new ArrayList<EdgeNetworkBenefit>();

		/*
		 * Populate/calculate the benefits and costs of each edge
		 */
		for (Network net : availableEdges) {
			EdgeNetwork en = (EdgeNetwork) net;
			EdgeNetworkBenefit estimatedBenefit = new EdgeNetworkBenefit();
			estimatedBenefit.network = en;
			for (List<ApplicationProvider> categoryASPs : c.appsUsed.values()) {
				for (ApplicationProvider asp : categoryASPs) {
					AppBenefitCalculator abc = c.appBenefitCalculator;
					// The % bandwidth we think will get through
					double expectedFraction = asp.getExpectedFraction(en);
					// our expected benefit given that congestion
					double expectedBenefit = abc.calculateBenefit(c, asp, expectedFraction);
					estimatedBenefit.sumAppBenefits += expectedBenefit;
				}
			}
			/*
			 * add 1 to edgeBenefits; shouldn't bias calc but still allows
			 * consumer to select the cheapest Edge Network rather than dividing
			 * zero by the price.
			 */
			estimatedBenefit.sumAppBenefits++;

			// estimatedBenefit now has the sum of all ASPs
			edgeBenefits.add(estimatedBenefit);
		}

		// Find the edge with the highest density that isn't more than our price
		// limit
		EdgeNetwork edgeToSelect = null;
		double higestBenefitDensity = 0D;
		for (EdgeNetworkBenefit enb : edgeBenefits) {
			if (enb.density() > higestBenefitDensity && enb.network.getPriceFuture() <= c.profile.getMaxNetworkPrice()) {
				higestBenefitDensity = enb.density();
				edgeToSelect = enb.network;
			}
		}

		// Finally! Consume that network.
		c.edgeNetwork.set(edgeToSelect);

	}

}