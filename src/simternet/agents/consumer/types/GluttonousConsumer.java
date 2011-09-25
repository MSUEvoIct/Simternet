package simternet.agents.consumer.types;

import java.io.Serializable;
import java.util.Collection;

import sim.util.Int2D;
import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.Consumer;
import simternet.engine.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * SimpleConsumer subscrubes to all networks and all applications, regardless of
 * cost. Uses each application on each network on which it is subscribed.
 * 
 * @author kkoning
 * 
 */
public class GluttonousConsumer extends Consumer implements Serializable {

	/**
	 * Always uses the same application usage variables, regardless of
	 * application.
	 */
	private static final long	serialVersionUID	= 1L;

	public GluttonousConsumer(Simternet s, Int2D location, Double population) {
		super(s, location, population, null, null, null, null);
	}

	/*
	 * Consume all applications, once on each network.
	 */
	@Override
	protected void consumeApplications() {
		Collection<Network> localEdgeNetworks = s.getNetworks(null, EdgeNetwork.class, getLocation());

		Collection<ApplicationProvider> asps = s.getASPs();

		for (ApplicationProvider asp : asps) {
			for (Network network : localEdgeNetworks) {
				consumeApplication(asp, (EdgeNetwork) network);
			}
		}

	}

	/*
	 * Just get all the edge networks at this location and consume each one.
	 */
	@Override
	protected void consumeNetworks() {
		Collection<Network> localEdgeNetworks = s.getNetworks(null, EdgeNetwork.class, getLocation());

		for (Network edgeNetwork : localEdgeNetworks) {
			consumeNetwork((EdgeNetwork) edgeNetwork);
		}
	}

	/*
	 * SimpleConsumer always uses every network
	 */
	@Override
	public Boolean usesNetwork(EdgeNetwork network) {
		return true;
	}

}
