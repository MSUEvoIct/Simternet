package simternet.consumer;

import java.io.Serializable;

import simternet.application.ApplicationProvider;

/**
 * Given a specified consumer, edge network, and application, calculate the
 * benefit received by the customer.
 * 
 * Default is (quality^0.5)*(bandiwidthUsed^0.5)*congestionRatio
 * 
 * @author kkoning
 * 
 */
public class AppBenefitCalculator implements Serializable {

	private static final long			serialVersionUID	= 1L;
	private static AppBenefitCalculator	singleton;

	public static AppBenefitCalculator getSingleton() {
		if (AppBenefitCalculator.singleton == null)
			AppBenefitCalculator.singleton = new AppBenefitCalculator();
		return AppBenefitCalculator.singleton;
	}

	public Double congestedBenefit(Consumer c, ApplicationProvider app, Double congestionRatio) {

		Double uncongestedBenefit = this.uncongestedBenefit(c, app);
		return uncongestedBenefit * congestionRatio;
	}

	public Double uncongestedBenefit(Consumer c, ApplicationProvider asp) {
		Double benefit = 0.0;
		benefit = Math.pow(asp.getQuality(), 0.5) * Math.pow(asp.getBandwidth(), 0.5);
		return benefit;
	}

}
