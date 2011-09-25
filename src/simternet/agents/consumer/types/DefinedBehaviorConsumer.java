package simternet.agents.consumer.types;

import java.io.Serializable;

import sim.util.Int2D;
import simternet.agents.consumer.Consumer;
import simternet.agents.consumer.behavior.DefaultAppBenefitCalculator;
import simternet.agents.consumer.behavior.GreedyAppManager;
import simternet.engine.Simternet;
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

	public DefinedBehaviorConsumer(Simternet s, Int2D location, Double population) {
		super(s, location, population, new NetworkMiser(), new GreedyAppManager(), new DefaultAppBenefitCalculator(),
				null);
	}

}
