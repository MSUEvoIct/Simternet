/**
 * 
 */
package simternet.consumer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import simternet.application.ApplicationProvider;
import simternet.network.EdgeNetwork;

class AppBenefit {
	ApplicationProvider	app;
	Double						benefit;
	Double						cost;
	EdgeNetwork					onNetwork;

	/**
	 * Reduces the benefit received if there is congestion
	 * 
	 * @return reduced benefit
	 */
	Double congest(Double benefit) {
		// Without a target network, assume zero congestion
		if (this.onNetwork == null)
			return benefit;
		// TODO: Document / parameterize this. Currently scales
		// benefit proportionally by congestion.
		Double congestionRatio = this.app.getCongestionRatio(this.onNetwork);
		if (congestionRatio == null)
			Logger.getRootLogger().log(Level.ERROR, "should have had a non-null congestion ratio");
		return benefit * congestionRatio;
	}

	Double density() {
		return this.congest(this.benefit) / this.cost;
	}

}