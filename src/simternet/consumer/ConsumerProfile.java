package simternet.consumer;

/**
 * ConsumerProfile objects store the attributes associated with each consumer
 * class. Since each location will have its own instance of a given Consumer
 * Class, this allows us to store the profile data in on e place rather than
 * once per map location.
 * 
 * @author kkoning
 * 
 */
public class ConsumerProfile {
	/**
	 * True if this consumer class is an "early adopter."
	 */
	public boolean earlyAdopter;
	/**
	 * The proportion of this period's consumption which is determined by the
	 * last period's consumption. (i.e., momentum or transaction costs)
	 */
	public Double switchSpeed;
}
