package simternet.nsp;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.consumer.AbstractConsumerClass;
import simternet.ecj.EvolvableAgent;
import simternet.ecj.SimternetGPIndividual;
import simternet.ecj.problems.PriceEdgeNetworkProblem;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;
import ec.Individual;
import ec.app.tutorial4.DoubleData;

public class GPNetworkProvider extends AbstractNetworkProvider implements EvolvableAgent {

	private static final long		serialVersionUID	= 1L;
	protected SimternetGPIndividual	ind;

	public GPNetworkProvider(Simternet simternet) {
		super(simternet);
		this.investmentStrategy = new BuildEverywhereStrategy(this);
	}

	@Override
	public Double getFitness() {
		// TODO: Actually think about the proper fitness measure.
		return this.financials.getAssetsLiquid();
	}

	@Override
	public Individual getIndividual() {
		return this.ind;
	}

	@Override
	public Double getPrice(Class<? extends AbstractNetwork> cl, AbstractConsumerClass acc, Int2D location) {

		DoubleData d = new DoubleData();
		d.x = 0;

		AbstractEdgeNetwork aen = (AbstractEdgeNetwork) this.getNetworkAt(cl, location);

		PriceEdgeNetworkProblem penp = new PriceEdgeNetworkProblem(aen, acc);

		this.ind.trees[0].child.eval(null, 0, d, null, this.ind, penp);

		return d.x;
	}

	@Override
	public void setIndividual(Individual i) {
		this.ind = (SimternetGPIndividual) i;
		this.ind.setAgent(this);
	}

	@Override
	protected void setPrices() {
		// Do nothing, let GP respond to getPrices.
	}

}
