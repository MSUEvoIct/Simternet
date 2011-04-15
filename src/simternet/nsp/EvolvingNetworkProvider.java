package simternet.nsp;

import java.io.Serializable;
import java.util.Date;

import simternet.Simternet;
import xcs.Environment;
import xcs.MyEnvironment;
import xcs.MyXCS;
import xcs.XCSConstants;

public class EvolvingNetworkProvider extends NetworkProvider implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private boolean				firstRun			= true;
	private boolean				hasLCSRun			= false;
	/**
	 * Price of service for all tiles
	 */
	private Double				price				= 15.0;

	private MyXCS				xcs;

	public EvolvingNetworkProvider(Simternet s) {
		super(s);
		this.pricingStrategy = new EvolvingPricingStrategy(this, this.price);
		this.investmentStrategy = new BuildEverywhereStrategy(this);
		// Create new XCS object
		String envFileString = null;
		Environment e = null;
		XCSConstants.setSeed(1 + (new Date()).getTime() % 10000);
		e = new MyEnvironment();
		this.xcs = new MyXCS(e);
		this.xcs.setNumberOfTrials(10000);
		this.xcs.setNumberOfExperiments(2);
		this.xcs.init();
		System.out.println("Evolving!");
	}

	// @Override
	// public Double getPrice(Class<? extends Network> cl, AbstractConsumerClass
	// acc, Int2D location) {
	// this.runLCSIfNecessary();
	// return this.price;
	// }

	public void runLCSIfNecessary() {
		if (!this.hasLCSRun) {
			System.out.println("Running LCS placeholder and passing " + this.getDeltaRevenue()
					+ " as delta revenue at time step " + this.simternet.schedule.getSteps());

			this.hasLCSRun = true;

			// Take a look at rules that LCS generates!
			// String env = new
			// String(Long.toBinaryString(Math.round(this.financials.getTotalRevenue())));
			String env = new String(Long.toBinaryString(Math.round(this.getDeltaRevenue())));
			// String(Long.toBinaryString(Math.round(this.price)));
			String pad = "";
			for (int x = 0; x < (20 - env.length()); x++)
				pad += "0";
			// env = String.format("%020d", Long.parseLong(env));
			env = pad + env;
			this.price *= this.xcs.doExternalLCS(env, this.getDeltaRevenue());
			System.out.println("The price is:" + this.price);
		}
	}

	@Override
	public void update() {
		super.update();
		this.hasLCSRun = false;
	}
}
