package simternet.consumer;

import java.io.Serializable;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.network.EdgeNetwork;

/**
 * Uses only the cheapest network connection at his location. Uses all
 * applications on that connection.
 * 
 * @author kkoning
 * 
 */
public class DefinedBehaviorConsumer extends Consumer implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public EdgeNetwork			myNetwork;

	public DefinedBehaviorConsumer(Simternet s, Int2D location, Double population, ConsumerProfile profile) {
		super(s, location, population, profile, new NetworkMiser(), new GreedyAppManager(), new AppBenefitCalculator());
	}

}
