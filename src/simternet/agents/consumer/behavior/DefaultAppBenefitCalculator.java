package simternet.agents.consumer.behavior;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.AppBenefitCalculator;
import simternet.agents.consumer.Consumer;
import simternet.network.EdgeNetwork;

/**
 * Default is (quality^0.5)*(congestionRatio * 2 - 1)
 * 
 * If congestion is greater than 50%, treat the applicaiton as having a negative
 * benefit. (i.e., it won't be consumed)
 * 
 * @author kkoning
 * 
 */
public class DefaultAppBenefitCalculator implements AppBenefitCalculator {
	private static final long			serialVersionUID	= 1L;

	// Default, singleton factory
	private static AppBenefitCalculator	singleton;

	public static AppBenefitCalculator getSingleton() {
		if (DefaultAppBenefitCalculator.singleton == null) {
			DefaultAppBenefitCalculator.singleton = new DefaultAppBenefitCalculator();
		}
		return DefaultAppBenefitCalculator.singleton;
	}

	/**
	 * @param c
	 * @param asp
	 * @param expectedFraction
	 *            The portion of requested usage the consumer expects will make
	 *            it through the network accounting for congestion
	 * @return
	 */
	@Override
	public double calculateBenefit(Consumer c, ApplicationProvider asp, EdgeNetwork net) {
		// MODEL CRITICAL MATH
		Double expectedFraction = asp.getExpectedFraction(net);

		// If there's no information on what the congestion is, assume it's
		// zero.
		if (expectedFraction == null) {
			expectedFraction = 1.0;
		}

		double adjustedExpectedFraction = expectedFraction * 2 - 1;

		double benefit = Math.pow(asp.getQuality(), 0.5) * adjustedExpectedFraction;
		return benefit;

	}
}
