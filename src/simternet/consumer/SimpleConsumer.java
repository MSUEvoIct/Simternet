package simternet.consumer;

import java.io.Serializable;
import java.util.Collection;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;
import simternet.network.NetFlow;

/**
 * SimpleConsumer subscrubes to all networks and all applications, regardless of
 * cost.
 * 
 * TODO: When using network, split traffic equally between all providers.
 * 
 * @author kkoning
 * 
 */
public class SimpleConsumer extends AbstractConsumerClass implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleConsumer(Simternet s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void consumeApplications() {
		for (ApplicationServiceProvider asp : this.s
				.getApplicationServiceProviders())
			for (Int2D location : this.s.allLocations()) {
				NetFlow nf = new NetFlow();
				nf.amount = this.population.get(location.x, location.y);
				asp.processUsage(nf);
			}
	}

	/*
	 * Nothing is done here. Instead, the consumption function has been
	 * overriden to ignore applicationServiceSubscriptions and always consume.
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
		Collection<AbstractNetwork> networks = this.s.getNetworks(null,
				AbstractEdgeNetwork.class, null);

		for (AbstractNetwork net : networks)
			this.networkSubscriptions.setObjectLocation(net, net.getLocation());
	}

}
