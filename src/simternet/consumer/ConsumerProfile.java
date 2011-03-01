package simternet.consumer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import simternet.application.AppCategory;
import simternet.application.ApplicationServiceProvider;

/**
 * ConsumerProfile objects store the attributes associated with each consumer
 * class. Since each location will have its own instance of a given Consumer
 * Class, this allows us to store the profile data in on e place rather than
 * once per map location.
 * 
 * @author kkoning
 * 
 */
public class ConsumerProfile implements Serializable {

	private static final long			serialVersionUID	= 1L;

	private Map<AppCategory, Double>	applicationBudgets	= new HashMap<AppCategory, Double>();

	/**
	 * True if this consumer class is an "early adopter."
	 */
	private boolean						earlyAdopter;

	private Double						maxNetworkPrice		= 70.0;

	/**
	 * The proportion of this period's consumption which is determined by the
	 * last period's consumption. (i.e., momentum or transaction costs)
	 */
	private Double						switchSpeed;

	/**
	 * Create a default consumer profile for testing purposes
	 */
	public ConsumerProfile() {

	}

	public Double getAppCategoryBudget(AppCategory ac) {
		Double amount = this.applicationBudgets.get(ac);
		if (amount == null)
			return 0.0;
		return amount;
	}

	/**
	 * benefit = quality ^ alpha * expected congestion
	 * 
	 * @param asp
	 * @return
	 */
	public Double getApplicationBenefit(ApplicationServiceProvider asp) {
		// TODO: Parameterize alpha
		Double preCongestionBenefit = Math.pow(asp.getQuality(), 0.7);
		return preCongestionBenefit;
	}

	public Double getMaxNetworkPrice() {
		return this.maxNetworkPrice;
	}
}
