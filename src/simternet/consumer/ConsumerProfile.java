package simternet.consumer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import simternet.application.AppCategory;

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

	private static final long		serialVersionUID	= 1L;

	private static ConsumerProfile	singleton;

	public static ConsumerProfile getSingleton() {
		if (ConsumerProfile.singleton == null)
			ConsumerProfile.singleton = new ConsumerProfile();
		return ConsumerProfile.singleton;
	}

	private Map<AppCategory, Double>	applicationBudgets	= new HashMap<AppCategory, Double>();

	/**
	 * True if this consumer class is an "early adopter."
	 */
	protected boolean					earlyAdopter;

	private Double						maxNetworkPrice		= 70.0;

	/**
	 * The proportion of this period's consumption which is determined by the
	 * last period's consumption. (i.e., momentum or transaction costs)
	 */
	protected Double					switchSpeed;

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

	public Double getMaxNetworkPrice() {
		return this.maxNetworkPrice;
	}
}
