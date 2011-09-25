/**
 * 
 */
package simternet.agents.consumer;

import simternet.agents.asp.ApplicationProvider;
import simternet.network.EdgeNetwork;

class AppBenefit {
	public ApplicationProvider	asp;
	public EdgeNetwork			onNetwork;

	public Double				benefit;
	public Double				cost;

	Double density() {
		return benefit / cost;
	}

}