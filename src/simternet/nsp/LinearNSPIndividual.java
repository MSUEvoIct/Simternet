package simternet.nsp;

import ec.vector.FloatVectorIndividual;

/**
 * 
 * @author kkoning
 * 
 */
public class LinearNSPIndividual extends FloatVectorIndividual implements
		NSPIndividual {
	private static final long serialVersionUID = 1L;
	
	// guess in range ~[-1,1] dev 0.1
	static final int POS_EDGEPROBCONSTANT = 0;
	static final int POS_EDGEPROBEDGES = 1;

	// guess in range ~[0,30] dev 5
	static final int POS_EDGEPRICECONSTANT = 2;
	
	// guess in range ~[-10,10] dev 4
	static final int POS_EDGEPRICEEDGES = 3;
	
	// guess in range ~[-0.2,0.2] dev 0.02
	static final int POS_EDGEPRICE_PERCENT_POP = 4;
	static final int POS_EDGEPRICE_PERCENT_SUBS = 5;
	
	// quite uncertain as to range, let's start with e^, [-7,0]
	static final int POS_BACKBONEPRICE_CONSTANT_POS = 6;
	static final int POS_BACKBONEPRICE_CONSTANT_NEG = 7;
	static final int POS_BACKBONEPRICE_VALUETERM_POS = 8;
	static final int POS_BACKBONEPRICE_VALUETERM_NEG = 9;
	
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
		
		double constantTerm = Math.exp(genome[POS_BACKBONEPRICE_CONSTANT_POS])
				- Math.exp(genome[POS_BACKBONEPRICE_CONSTANT_NEG]);
		
		double valueCoef = Math.exp(genome[POS_BACKBONEPRICE_VALUETERM_POS])
				- Math.exp(genome[POS_BACKBONEPRICE_VALUETERM_NEG]);
		
		double price = constantTerm + valueCoef * value;
		
		return price;
	}

}
