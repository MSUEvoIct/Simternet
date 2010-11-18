package simternet.nsp;

import java.io.Serializable;

import simternet.Simternet;

public class EvolvingNetworkProvider extends AbstractNetworkProvider implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean hasLCSRun = false;

	/**
	 * Price of service for all tiles
	 */
	private Double price = 15.0;

	public EvolvingNetworkProvider(Simternet s) {
		super(s);
		this.pricingStrategy = new EvolvingPricingStrategy(this, this.price);
		this.investmentStrategy = new BuildEverywhereStrategy(this);
		// Create new XCS object
	}

	// Add code to run XCS (if it hasn't been run yet this step)

	@Override
	public void update() {
		super.update();
		this.hasLCSRun = false;
	}
}
