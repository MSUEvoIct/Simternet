package simternet.consumer;

import simternet.application.ApplicationProvider;
import simternet.network.EdgeNetwork;

/**
 * Given a specified consumer, edge network, and application, calculate the
 * benefit received by the customer.
 * 
 * Default is (quality^0.5)*congestionRatio
 * 
 * @author kkoning
 * 
 */
public class AppBenefitCalculator {

	private static AppBenefitCalculator	singleton;

	public static AppBenefitCalculator getSingleton() {
		if (AppBenefitCalculator.singleton == null)
			AppBenefitCalculator.singleton = new AppBenefitCalculator();
		return AppBenefitCalculator.singleton;
	}

	public Double calculateBenefit(Consumer c, EdgeNetwork network, ApplicationProvider app) {

		Double benefit = 0.0;
		benefit = Math.pow(app.getQuality(), 0.5);

		if (network != null) {
			Double congestionRatio = app.getCongestionRatio(network);
			benefit = benefit * congestionRatio;
		}

		return benefit;
	}
}
