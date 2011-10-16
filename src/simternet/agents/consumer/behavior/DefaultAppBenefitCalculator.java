package simternet.agents.consumer.behavior;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.AppBenefitCalculator;
import simternet.agents.consumer.Consumer;
import simternet.engine.TraceConfig;

/**
 * Default is (quality^0.5)*(congestionRatio * 2 - 1)
 * 
 * If congestion is greater than 50%, treat the applicaiton as having a negative
 * benefit. (i.e., it won't be consumed)
 * 
 * @author kkoning
 * 
 */
public class DefaultAppBenefitCalculator extends AppBenefitCalculator {
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
	 * XXX: MODEL CRITICAL MATH
	 * 
	 * Benefit is calculated as quality^0.5 * (fraction * 2 - 1)
	 * 
	 * @param c
	 *            The consumer
	 * @param asp
	 *            The application
	 * @param fractionReceived
	 *            The ration of bandwidth received to that requested by the
	 *            application usage The portion of requested usage the consumer
	 *            expects will make it through the network accounting for
	 *            congestion
	 * @return The benefit received
	 */
	@Override
	public double calculateBenefit(Consumer c, ApplicationProvider asp, Double fractionReceived) {
		// If there's no information on what the congestion is, assume it's
		// zero.
		if (fractionReceived == null) {
			fractionReceived = 1.0;
		}
		//
		double adjustedExpectedFraction = fractionReceived * 2 - 1;
		double benefit = Math.pow(asp.getQuality(), 0.5) * adjustedExpectedFraction;

		double randomPreference = Math.abs(asp.diversityFactor - c.diversityFactor);
		benefit = benefit * randomPreference;

		if (TraceConfig.modelMath.aspBenefit) {
			TraceConfig.out.println(c + " calculated actual benefit for " + asp.getName() + " as " + benefit
					+ ", based on");
			TraceConfig.out.println("  quality = " + asp.getQuality());
			TraceConfig.out.println("  consumer diversity factor = " + c.diversityFactor);
			TraceConfig.out.println("  asp diversity factor = " + asp.diversityFactor);
			TraceConfig.out.println("  expected % not lost to congestion = " + fractionReceived * 100);
		}

		return benefit;
	}

}
