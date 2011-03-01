package simternet.consumer;

import java.io.Serializable;
import java.util.Collection;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * SimpleConsumer subscrubes to all networks and all applications, regardless of
 * cost. Uses each application on each network on which it is subscribed.
 * 
 * TODO: When using network, split traffic equally between all providers.
 * 
 * @author kkoning
 * 
 */
public class SimpleConsumer extends AbstractConsumerClass implements
		Serializable {

	/**
	 * Always uses the same application usage variables, regardless of
	 * application.
	 */
	private static final long serialVersionUID = 1L;

	public SimpleConsumer(Simternet s, Int2D location, Double population,
			ConsumerProfile profile) {
		super(s, location, population, profile);
	}

	/*
	 * Consume all applications, once on each network.
	 */
	@Override
	protected void consumeApplications() {
		Collection<Network> localEdgeNetworks = this.s.getNetworks(
				null, EdgeNetwork.class, this.getLocation());

		Collection<ApplicationServiceProvider> asps = this.s
				.getASPs();

		for (ApplicationServiceProvider asp : asps)
			for (Network network : localEdgeNetworks)
				this.consumeApplication(asp, (EdgeNetwork) network);

	}

	/*
	 * Just get all the edge networks at this location and consume each one.
	 */
	@Override
	protected void consumeNetworks() {
		Collection<Network> localEdgeNetworks = this.s.getNetworks(
				null, EdgeNetwork.class, this.getLocation());

		for (Network edgeNetwork : localEdgeNetworks)
			this.consumeNetwork((EdgeNetwork) edgeNetwork);
	}

	/*
	 * Do nothing. We're always going to use every application.
	 */
	@Override
	protected void manageApplications() {
	}

	/*
	 * Do nohing. We're always going to use every network.
	 */
	@Override
	protected void manageNetworks() {
	}

	/*
	 * SimpleConsumer always uses every network
	 */
	@Override
	public Boolean usesNetwork(EdgeNetwork network) {
		return true;
	}

}
