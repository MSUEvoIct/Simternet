package simternet.consumer;

import java.io.Serializable;
import java.util.Collection;

import org.apache.log4j.Logger;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.TraceConfig;
import simternet.application.ApplicationProvider;

/**
 * Uses only the cheapest network connection at his location. Uses all
 * applications on that connection.
 * 
 * @author kkoning
 * 
 */
public class AllAppsNetworkMiser extends Consumer implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public AllAppsNetworkMiser(Simternet s, Int2D location, Double population, ConsumerProfile profile) {
		super(s, location, population, profile, NetworkMiser.getSingleton(), null, null);
	}

	/*
	 * Consume ALL applications.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.consumer.Consumer#consumeApplications()
	 */
	@Override
	protected void consumeApplications() {

		// if we are not subscribed to a network, we cannot consume any network
		// applications
		if (this.edgeNetwork.get() == null)
			return;

		Collection<ApplicationProvider> allApps = this.s.getASPs();

		for (ApplicationProvider asp : allApps) {
			// Use that app on the network we're subscribed to
			this.consumeApplication(asp, this.edgeNetwork.get());
			if (TraceConfig.consumerUsedApp && Logger.getRootLogger().isTraceEnabled())
				Logger.getRootLogger().trace(this + " consumed " + asp);
		}

	}
}
