package simternet.nsp;

import java.io.Serializable;
import java.util.Date;

import simternet.Simternet;
import xcs.Environment;
import xcs.MyEnvironment;
import xcs.MyXCS;
import xcs.XCSConstants;

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
		String envFileString = null;
		Environment e = null;
		XCSConstants.setSeed(1 + (new Date()).getTime() % 10000);
		e = new MyEnvironment();
		MyXCS xcs = new MyXCS(e, "out.txt");
		xcs.setNumberOfTrials(10000);
		xcs.setNumberOfExperiments(2);
	}

	// Add code to run XCS (if it hasn't been run yet this step)

	@Override
	public void update() {
		super.update();
		this.hasLCSRun = false;
	}
}
