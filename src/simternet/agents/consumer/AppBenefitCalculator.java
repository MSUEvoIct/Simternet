package simternet.agents.consumer;

import java.io.Serializable;

import simternet.agents.asp.ApplicationProvider;
import simternet.network.EdgeNetwork;

/**
 * Given a specified consumer, edge network, and application, calculate the
 * benefit received by the customer.
 * 
 * @author kkoning
 * 
 */
public interface AppBenefitCalculator extends Serializable {
	/**
	 * Calculate the benefit to consumer c of using application asp on
	 * edgeNetwork net
	 * 
	 * @param c
	 * @param asp
	 * @param expectedFraction
	 * @return
	 */
	public abstract double calculateBenefit(Consumer c, ApplicationProvider asp, EdgeNetwork net);
}
