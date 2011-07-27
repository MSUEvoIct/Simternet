package simternet.application;

import sim.engine.SimState;
import simternet.Simternet;
import simternet.ecj.EvolvableAgent;
import simternet.ecj.SimternetGPIndividual;
import simternet.network.BackboneLink;
import simternet.network.RoutingProtocolConfig;
import simternet.nsp.NetworkProvider;
import ec.Individual;

public class GPApplicationProvider extends ApplicationProvider implements EvolvableAgent {

	private static final long		serialVersionUID	= 1L;
	private BandwidthStrategy		bandwidthStrategy;
	protected SimternetGPIndividual	ind;

	public GPApplicationProvider(Simternet s) {
		// XXX: FIX
		super(s, AppCategory.INFORMATION);
	}

	@Override
	public Double getFitness() {
		// TODO: Think about the proper fitness measure.
		return this.financials.getNetWorth();
	}

	@Override
	public Individual getIndividual() {
		return this.ind;
	}

	public String printQualityTree() {
		return this.ind.trees[0].toString();

	}

	public String printTransitPurchaseTree() {
		return this.ind.trees[1].toString();

	}

	@Override
	public void setIndividual(Individual i) {
		this.ind = (SimternetGPIndividual) i;
		this.ind.setAgent(this);
		this.qualityStrategy = new GPQualityStrategy(this, this.ind, this.ind.trees[0]);
		this.transitStrategy = new GPTransitPurchaseStrategy(this, this.ind, this.ind.trees[1]);
		this.bandwidthStrategy = new GPBandwidthStrategy(this, this.ind, this.ind.trees[2]);
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		// TODO: Create generic strategy and move this code to
		// ApplicationProvider.step()
		for (NetworkProvider nsp : this.s.getNetworkServiceProviders()) {
			Double bw = 0.0;
			Double totalPrice = 0.0;
			Double price = nsp.getASPTransitPrice(this);
			Double gpBW = this.transitStrategy.bandwidthToPurchase(nsp, price);
			Double totalGPPrice = gpBW * price;
			if (totalGPPrice * 10 > this.financials.getNetWorth())
				// Can't spend more than 10% of net worth each step;
				bw = (this.financials.getNetWorth() / 10) / price;
			totalPrice = bw * price;

			BackboneLink bl;
			bl = this.datacenter.getEgressLink(nsp.getBackboneNetwork());
			if (bl == null) {
				this.datacenter.createEgressLinkTo(nsp.getBackboneNetwork(), bw, RoutingProtocolConfig.TRANSIT);
				bl = this.datacenter.getEgressLink(nsp.getBackboneNetwork());
			}

			// TODO Clean this up
			bl.setBandwidth(bw);
			this.financials.earn(-totalPrice);
			nsp.financials.earn(totalPrice);

		}
		// TODO: Create generic strategy and move this code to
		// ApplicationProvider.step()
		Double bwIncrease = this.bandwidthStrategy.increaseBandwidth();
		this.bandwidth.increase(bwIncrease);

	}

}
