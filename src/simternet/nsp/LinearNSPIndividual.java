package simternet.nsp;

import ec.vector.FloatVectorIndividual;

/**
 * 
 * @author kkoning
 * 
 */
public class LinearNSPIndividual extends FloatVectorIndividual implements
		NSPIndividual {

	static final int POS_EDGEPROBCONSTANT = 0;
	static final int POS_EDGEPROBEDGES = 1;

	static final int POS_EDGEPRICECONSTANT = 2;
	static final int POS_EDGEPRICEEDGES = 3;
	static final int POS_EDGEPRICE_PERCENT_POP = 4;
	static final int POS_EDGEPRICE_PERCENT_SUBS = 5;
	
	static final int POS_BACKBONEPRICE_CONSTANT = 6;
	static final int POS_BACKBONEPRICE_VALUETERM = 7;

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
		edgePrice += genome[POS_EDGEPRICE_PERCENT_POP]
				* eps.percentOfPopulation;
		edgePrice += genome[POS_EDGEPRICE_PERCENT_SUBS]
				* eps.percentOfSubscriptions;

		return edgePrice;
	}

	@Override
	public double priceBackboneLink(BackbonePricingStimulus bps) {
		
		double value = Math.pow(bps.aspQuality, bps.qualityValueExponent);
		double price = genome[POS_BACKBONEPRICE_CONSTANT] + genome[POS_BACKBONEPRICE_VALUETERM] * value;
		
		return price;
	}

}
