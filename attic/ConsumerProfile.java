package simternet.agents.consumer;


/**
 * ConsumerProfile objects store the attributes associated with each consumer
 * class. Since each location will have its own instance of a given Consumer
 * Class, this allows us to store the profile data in on e place rather than
 * once per map location.
 * 
 * @author kkoning
 * 
 */
//public class ConsumerProfile implements Serializable {
//
//	private static final long		serialVersionUID	= 1L;
//
//	private static ConsumerProfile	singleton;
//
//	public static ConsumerProfile getSingleton() {
//		if (ConsumerProfile.singleton == null) {
//			ConsumerProfile.singleton = new ConsumerProfile();
//		}
//		return ConsumerProfile.singleton;
//	}
//
//	private Map<AppCategory, Double>	applicationBudgets	= new HashMap<AppCategory, Double>();
//
//	/**
//	 * Create a default consumer profile for testing purposes
//	 */
//	public ConsumerProfile() {
//
//	}
//
//	public Double getAppCategoryBudget(AppCategory ac) {
//		Double amount = applicationBudgets.get(ac);
//		if (amount == null)
//			return 0.0;
//		return amount;
//	}
//
// }
