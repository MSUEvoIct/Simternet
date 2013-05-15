package simternet.nsp;

import ec.vector.FloatVectorIndividual;

/**
 * 
 * @author kkoning
 * 
 */
public class TrivialNSPIndividual extends FloatVectorIndividual implements
		NSPIndividual {
	private static final long serialVersionUID = 1L;

	static final int POS_EDGEPROBCONSTANT = 0;
	static final int POS_EDGEPRICECONSTANT = 1;

	@Override
	public boolean buildEdge(EdgeBuildingStimulus ebs) {
		float buildProb = 0;
		buildProb += genome[POS_EDGEPROBCONSTANT];

		float threshold = ebs.random.nextFloat();
		if (buildProb > threshold)
			return true;
		else
			return false;
	}

	@Override
	public double priceEdge(EdgePricingStimulus eps) {
		float edgePrice = 0;
		edgePrice += genome[POS_EDGEPRICECONSTANT];

		return edgePrice;
	}

	@Override
	public double priceBackboneLink(BackbonePricingStimulus bps) {
		// TODO Just use the unit price for now.
		return 1;
	}

}
