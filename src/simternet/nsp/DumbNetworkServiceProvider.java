package simternet.nsp;

import java.io.Serializable;

import simternet.Simternet;

/**
 * @author kkoning
 * 
 *         This NSP only offers one type of network: SimpleNetwork, and it
 *         always charges a uniform, unvarying price.
 * 
 */
@SuppressWarnings("serial")
public class DumbNetworkServiceProvider extends NetworkProvider implements Serializable {

	/**
	 * DumbNetworkServiceProviders always offer service for exactly 15.0
	 */
	private static Double	price	= 15.0;

	public DumbNetworkServiceProvider(Simternet s) {
		super(s);
		this.pricingStrategy = new ConstantPricingStrategy(this, DumbNetworkServiceProvider.price);
		this.investmentStrategy = new BuildEverywhereStrategy(this);
		// List<Class<? extends EdgeNetwork>> networks = new ArrayList<Class<?
		// extends EdgeNetwork>>();
		// networks.add(SimpleEdgeNetwork.class);
		//
		// this.investmentStrategy = new ScoringInvestmentStrategy(this,
		// networks, 0.0);
	}

}
