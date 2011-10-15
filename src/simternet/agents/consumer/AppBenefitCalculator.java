package simternet.agents.consumer;

import java.io.Serializable;
import java.util.List;

import simternet.agents.asp.ApplicationProvider;
import simternet.network.EdgeNetwork;

/**
 * Given a specified consumer, edge network, and application, calculate the
 * benefit received by the customer.
 * 
 * @author kkoning
 * 
 */
public abstract class AppBenefitCalculator implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Calculate the benefit to consumer c of using application asp on
	 * edgeNetwork net
	 * 
	 * @param c
	 *            The consumer
	 * @param asp
	 *            The application
	 * @param fractionReceived
	 *            The ration of bandwidth requested to received
	 * @return the benefit of using the application
	 */
	public abstract double calculateBenefit(Consumer c, ApplicationProvider asp, Double fractionReceived);

	/**
	 * Estimate the benefit that will be received by the customer from using the
	 * application on the specified edge network by assuming that congestion
	 * will remain constant. Include a random bonus to apps that are already
	 * being used, in order to make switching away from them a stochastic (and
	 * thus not synchronous between all similarly situated consumers) process.
	 * 
	 * @param c
	 *            The consumer
	 * @param asp
	 *            The application
	 * @param net
	 *            The edge network
	 * @return The estimated benefit of using the applicaiton on the specified
	 *         edge network
	 */
	public double estimateBenefit(Consumer c, ApplicationProvider asp, EdgeNetwork net) {
		Double expectedFraction = asp.getExpectedFraction(net);
		Double estimatedBenefit = calculateBenefit(c, asp, expectedFraction);
		List<ApplicationProvider> appsUsed = c.appsUsed.get(asp.getAppCategory());
		boolean usedLastPeriod = false;
		if (appsUsed != null) {
			usedLastPeriod = appsUsed.contains(asp);
		}

		if (usedLastPeriod) {
			double randomBonus = c.s.config.applicationUsageBonusRatio * c.s.random.nextDouble();
			estimatedBenefit *= 1 + randomBonus;
		}
		return estimatedBenefit;
	}

}
