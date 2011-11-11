package simternet.agents.consumer.behavior;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.AppBenefitCalculator;
import simternet.agents.consumer.Consumer;
import simternet.agents.consumer.NetManager;
import simternet.engine.Simternet;
import simternet.engine.TraceConfig;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * UtilityNetManager calculates the sum of expected benefits, X from application
 * providers it is currently using on each potential edge network.
 * 
 * It then calculates a willingness to pay for the network service based on this
 * benefit.
 * 
 * Finally, it chooses the network which give it the highest positive surplus.
 * 
 * The motivation behind trying this approach is that it connects higher
 * benefits from applications with a higher willingness to pay, and therefore
 * greater revenue from providers.
 * 
 * @author kkoning
 * 
 */
public class UtilityNetManager extends NetManager implements Serializable {

	private static UtilityNetManager	singleton;

	public static UtilityNetManager getSingleton() {
		if (UtilityNetManager.singleton == null) {
			UtilityNetManager.singleton = new UtilityNetManager();
		}
		return UtilityNetManager.singleton;
	}

	@Override
	public void manageNetworks(Consumer c) {

		if (TraceConfig.programFlow) {
			TraceConfig.out.println(this + " managing networks for " + c);
			TraceConfig.out.println("Current Network = " + c.getEdgeNetwork().get());
		}

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
					if (TraceConfig.modelMath.aspBenefit) {
						TraceConfig.out.println("Estimating benefit for " + asp.getName() + " on " + en);
					}

					double expectedBenefit = abc.estimateBenefit(c, asp, en);
					estimatedBenefit.sumAppBenefits += expectedBenefit;
				}
			}
			/*
			 * add a small number to edgeBenefits; shouldn't bias calc but still
			 * allows consumer to select the cheapest Edge Network rather than
			 * dividing zero by the price.
			 */
			estimatedBenefit.sumAppBenefits += 0.00001;

			if (TraceConfig.modelMath.nspBenefit) {
				TraceConfig.out.println("Calculated estimated benefit for " + en + " of "
						+ estimatedBenefit.sumAppBenefits);
				TraceConfig.out
						.println("  (Will differ slightly even on identically performing networks, b/c/o random component to reused applications)");
			}

			// If this is the NSP Edge that we're currently using, give it a
			// random propotional bonus to
			// de-synchronize switching behavior.
			EdgeNetwork currentEdge = c.getEdgeNetwork().get();
			if (currentEdge != null) {
				if (currentEdge.equals(en)) {
					double randomBonus = c.s.config.networkUsageBonusRatio * c.s.random.nextDouble();
					estimatedBenefit.sumAppBenefits *= 1 + randomBonus;
					if (TraceConfig.modelMath.nspBenefit) {
						TraceConfig.out.println("As currently used edge, it receives a random bonus of " + randomBonus
								* 100 + "%, benefit now " + estimatedBenefit.sumAppBenefits);
					}
				}
			}

			// estimatedBenefit now has the sum of all ASPs
			edgeBenefits.add(estimatedBenefit);
		}

		// Find the edge with the highest surplus
		EdgeNetwork edgeToSelect = null;
		double highestSurplus = 0D;
		for (Consumer.EdgeNetworkBenefit enb : edgeBenefits) {
			double wtp = 10 + Math.pow(Math.E, enb.sumAppBenefits);

			if (TraceConfig.modelMath.nspBenefit) {
				TraceConfig.out.println("WTP(Ben=" + Simternet.nf.format(enb.sumAppBenefits) + ")="
						+ Simternet.nf.format(wtp));
			}

			double surplus = wtp - enb.network.getPriceFuture();

			if (surplus > highestSurplus) {
				highestSurplus = surplus;
				if (surplus > 0) {
					edgeToSelect = enb.network;
				}
			}

		}

		if (TraceConfig.kitchenSink) {
			TraceConfig.out.println("Winner is " + edgeToSelect);
		}

		// Finally! Consume that network.
		c.setEdgeNetwork(edgeToSelect);

	}
}
