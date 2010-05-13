package simternet.nsp;

import java.io.Serializable;

import simternet.Simternet;

public class RepeatedStackelburgNSP extends AbstractNetworkProvider implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RepeatedStackelburgNSP(Simternet s) {
		super(s);
		this.pricingStrategy = new RepeatedStackelbergPricingStrategy(this,
				this.networks);
		// this.investmentStrategy = new BuildEverywhereStrategy(this,
		// this.networks);
		this.investmentStrategy = new ScoringInvestmentStrategy(this,
				this.networks, 5.0, null);
	}

}
