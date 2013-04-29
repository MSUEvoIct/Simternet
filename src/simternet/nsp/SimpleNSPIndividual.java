package simternet.nsp;

import ec.vector.FloatVectorIndividual;




/**
 * 
 * @author kkoning
 *
 */
public class SimpleNSPIndividual extends FloatVectorIndividual implements
		NSPIndividual {

	static final int POS_EDGEPROBCONSTANT = 0;
	static final int POS_EDGEPROBEDGES = 1;
	
	static final int POS_EDGEPRICECONSTANT = 2;
	static final int POS_EDGEPRICEEDGES = 3;
	
	
	@Override
	public boolean buildEdge(EdgeBuildingStimulus ebs) {
		float buildProb = 0;
		buildProb += genome[POS_EDGEPROBCONSTANT];
		buildProb += genome[POS_EDGEPROBEDGES] * ebs.numEdges;

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
		edgePrice += genome[POS_EDGEPRICEEDGES] * eps.numEdges;

		return edgePrice;
	}

	@Override
	public double priceBackboneLink(BackbonePricingStimulus bps) {
		// TODO Just use the unit price for now.
		return 1;
	}

}
