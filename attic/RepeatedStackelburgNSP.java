package simternet.nsp;

import java.io.Serializable;

import simternet.Simternet;

@SuppressWarnings("serial")
public class RepeatedStackelburgNSP extends AbstractNetworkProvider {

	public RepeatedStackelburgNSP(Simternet s) {
		super(s);
		this.pricingStrategy = new RepeatedStackelbergPricingStrategy(this,
				this.edgeNetworks);
		this.investmentStrategy = new ScoringInvestmentStrategy(this,
				this.edgeNetworks, 5.0, null);
		// this.investmentStrategy = new BuildEverywhereStrategy(this,
		// this.networks);

	}

}
