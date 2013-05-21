package simternet.nsp;

import ec.vector.FloatVectorIndividual;

/**
 * 
 * @author kkoning
 * 
 */
public class MarketShareTargetNSPIndividual extends FloatVectorIndividual implements
		NSPIndividual {
	private static final long serialVersionUID = 1L;

	static final int POS_INITIALPRICE = 0;
	static final int POS_SHARETARGET = 1;
	static final int POS_PRICESCALAR = 2;
	
	static final float priceScalar = 0.1F; // XXX Hard Coded.
	

	@Override
	public boolean buildEdge(EdgeBuildingStimulus ebs) {
		return true;
	}

	@Override
	public double priceEdge(EdgePricingStimulus eps) {
		if (eps.currentPrice == 0)
			return genome[POS_INITIALPRICE];
		
		if (eps.percentOfPopulation < genome[POS_SHARETARGET])
			return eps.currentPrice - (eps.currentPrice * priceScalar);

		if (eps.percentOfPopulation >= genome[POS_SHARETARGET])
			return eps.currentPrice + (eps.currentPrice * priceScalar);

		// should be unreachable
		return eps.currentPrice;
	}

	@Override
	public double priceBackboneLink(BackbonePricingStimulus bps) {
		// TODO Just use the unit price for now.
		return 1;
	}

}
