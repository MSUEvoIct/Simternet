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
		double netWorth = financials.getNetWorth();
		if (netWorth > 0)
			return netWorth;
		else
			return 0.0;
	}

	@Override
	public Individual getIndividual() {
		return ind;
	}

	public String printQualityTree() {
		return ind.trees[0].toString();

	}

	public String printTransitPurchaseTree() {
		return ind.trees[1].toString();

	}

	@Override
	public void setIndividual(Individual i) {
		ind = (SimternetGPIndividual) i;
		ind.setAgent(this);
		qualityStrategy = new GPQualityStrategy(this, ind, ind.trees[0]);
		transitStrategy = new GPTransitPurchaseStrategy(this, ind, ind.trees[1]);
		bandwidthStrategy = new GPBandwidthStrategy(this, ind, ind.trees[2]);
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		// TODO: Create generic strategy and move this code to
		// ApplicationProvider.step()
		for (NetworkProvider nsp : s.getNetworkServiceProviders()) {
			Double bw = 0.0;
			Double totalPrice = 0.0;
			Double price = nsp.getASPTransitPrice(this);
			Double gpBW = transitStrategy.bandwidthToPurchase(nsp, price);
			Double totalGPPrice = gpBW * price;
			if (totalGPPrice * 3 > financials.getNetWorth()) {
				// Can't spend more than 33% of net worth each step;
				bw = financials.getNetWorth() / 3 / price;
			}
			totalPrice = bw * price;

			BackboneLink bl;
			bl = datacenter.getEgressLink(nsp.getBackboneNetwork());
			if (bl == null) {
				datacenter.createEgressLinkTo(nsp.getBackboneNetwork(), bw, RoutingProtocolConfig.TRANSIT);
				bl = datacenter.getEgressLink(nsp.getBackboneNetwork());
			}

			// TODO Clean this up
			bl.setBandwidth(bw);
			financials.earn(-totalPrice);
			nsp.financials.earn(totalPrice);

		}
		// TODO: Create generic strategy and move this code to
		// ApplicationProvider.step()
		// Double bwIncrease = this.bandwidthStrategy.increaseBandwidth();
		// this.bandwidth.increase(bwIncrease);

	}

}
