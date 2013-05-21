package simternet.nsp;

import ec.vector.FloatVectorIndividual;

public class LowestPriceNSPIndividual extends FloatVectorIndividual implements NSPIndividual {
	private static final long serialVersionUID = 1L;

	static final int POS_MINPRICE = 0;
	static final int POS_MAXPRICE = 1;
	
	@Override
	public boolean buildEdge(EdgeBuildingStimulus ebs) {
		return true;
	}

	@Override
	public double priceEdge(EdgePricingStimulus eps) {
		double tempPrice = eps.minOtherPrice;
		if (tempPrice < genome[POS_MINPRICE])
			tempPrice = genome[POS_MINPRICE];
		if (tempPrice > genome[POS_MAXPRICE])
			tempPrice = genome[POS_MAXPRICE];
		
		return tempPrice;
	}

	@Override
	public double priceBackboneLink(BackbonePricingStimulus bps) {
		return 1;
	}

	
	
	
}
