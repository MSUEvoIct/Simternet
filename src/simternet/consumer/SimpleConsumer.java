package simternet.consumer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;

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
	private static final ApplicationUsage au;
	private static final long serialVersionUID = 1L;

	static {
		au = new ApplicationUsage();
		SimpleConsumer.au.usageAmount.set(1.0);
		SimpleConsumer.au.congestionReceived.set(0.0);
		SimpleConsumer.au.update();
	}

	public SimpleConsumer(Simternet s, Int2D location, Double population,
			ConsumerProfile profile) {
		super(s, location, population, profile);
	}

	/*
	 * Don't track application usage, just consume from everyone on every
	 * network.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.consumer.AbstractConsumerClass#consumeApplications()
	 */
	@Override
	protected void consumeApplications() {

		for (Map.Entry<AbstractEdgeNetwork, NetworkUsageDetails> netMap : this.networkUsage
				.entrySet()) {

			AbstractEdgeNetwork aen = netMap.getKey();
			for (ApplicationServiceProvider asp : this.s
					.getApplicationServiceProviders())
				this.consumeApplication(aen, asp, SimpleConsumer.au);
		}

	}

	/*
	 * Nothing is done here. Instead, the consumption function has been
	 * overriden to ignore usage tracking variables and always consume.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.consumer.AbstractConsumerClass#manageApplications()
	 */
	@Override
	protected void manageApplications() {

	}

	/*
	 * Simply subscribe to <i>all</i> edge networks.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.consumer.AbstractConsumerClass#manageNetworks()
	 */
	@Override
	protected void manageNetworks() {

		// Get all edge networks at this location.
		Collection<AbstractNetwork> networks = this.s.getNetworks(null,
				AbstractEdgeNetwork.class, this.location);

		// For each of these networks,
		for (AbstractNetwork net : networks) {
			// these are all edge networks...
			AbstractEdgeNetwork aen = (AbstractEdgeNetwork) net;

			// if we aren't already subscribed to them,
			if (!this.networkUsage.containsKey(net)) {
				// do so. Every individual subscribes.
				NetworkUsageDetails nud = new NetworkUsageDetails();
				nud.subscribers.set(this.getPopultation());
				this.networkUsage.put(aen, nud);
			}
		}

	}

}
