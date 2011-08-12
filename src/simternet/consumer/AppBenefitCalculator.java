package simternet.consumer;

import java.io.Serializable;

import simternet.SimternetConfig;
import simternet.application.ApplicationProvider;

/**
 * Given a specified consumer, edge network, and application, calculate the
 * benefit received by the customer.
 * 
 * Default is (quality^0.5)*congestionRatio
 * 
 * @author kkoning
 * 
 */
public class AppBenefitCalculator implements Serializable {
	private static final long			serialVersionUID	= 1L;

	// Default, singleton factory
	private static AppBenefitCalculator	singleton;

	public static AppBenefitCalculator getSingleton() {
		if (AppBenefitCalculator.singleton == null) {
			AppBenefitCalculator.singleton = new AppBenefitCalculator();
		}
		return AppBenefitCalculator.singleton;
	}

	public double calculateBenefit(Consumer c, ApplicationProvider asp, Double expectedFraction) {
		return SimternetConfig.defaultApplicationBenefit(c, asp, expectedFraction);
	}

}
