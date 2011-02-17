package simternet.nsp;

import java.io.Serializable;
import java.util.Date;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;
import xcs.Environment;
import xcs.MyEnvironment;
import xcs.MyXCS;
import xcs.XCSConstants;

public class EvolvingNetworkProvider extends AbstractNetworkProvider implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

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
		MyXCS xcs = new MyXCS(e);
		xcs.setNumberOfTrials(10000);
		xcs.setNumberOfExperiments(2);
		System.out.println("Evolving!");
	}

	@Override
	public Double getPrice(Class<? extends AbstractNetwork> cl, AbstractConsumerClass acc, Int2D location) {
		this.runLCSIfNecessary();
		return this.price;
	}

	public void runLCSIfNecessary() {
		if (!this.hasLCSRun) {
			System.out.println("Running LCS placeholder and passing " + this.getDeltaRevenue()
					+ " as delta revenue at time step " + this.simternet.schedule.getSteps());

			this.price = 10.0; // will eventually come from LCS
			this.hasLCSRun = true;
			String aD = new String(Long.toBinaryString(Math.round(this.getDeltaRevenue())));
			// AgentData aD = new AgentData(this.getDeltaRevenue());
			this.xcs.doMutliStepSingleIncrementExperiment(aD);
		}
	}

	@Override
	public void update() {
		super.update();
		this.hasLCSRun = false;
	}
}
