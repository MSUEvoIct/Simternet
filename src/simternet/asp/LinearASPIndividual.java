package simternet.asp;

import ec.vector.FloatVectorIndividual;

public class LinearASPIndividual extends FloatVectorIndividual implements
		ASPIndividual {
	private static final long serialVersionUID = 1L;

	// Pricing
	public static final int POS_PRICE_CONSTANT_POS = 0;
	public static final int POS_PRICE_CONSTANT_NEG = 1;

	public static final int POS_PRICE_VALUE_TERM_POS = 2;
	public static final int POS_PRICE_VALUE_TERM_NEG = 3;

	// Investment in Quality
	public static final int POS_QUAL_CONSTANT_POS = 4;
	public static final int POS_QUAL_CONSTANT_NEG = 5;
	public static final int POS_QUAL_QUAL_POS = 6;
	public static final int POS_QUAL_QUAL_NEG = 7;

	
	// Purchasing of backbone bandwidth
	// Should probably range [-1..2]
	public static final int POS_BWQTY_TOTALPOP_POS = 8;
	public static final int POS_BWQTY_TOTALPOP_NEG = 9;

	public static final int POS_BWQTY_NSPCUSTOMERS_POS = 10;
	public static final int POS_BWQTY_NSPCUSTOMERS_NEG = 11;

	public static final int POS_BWQTY_ASPCUSTOMERS_POS = 12;
	public static final int POS_BWQTY_ASPCUSTOMERS_NEG = 13;
	
	public static final int POS_BWQTY_INTERSECTIONCUSTOMERS_POS = 14;
	public static final int POS_BWQTY_INTERSECTIONCUSTOMERS_NEG = 15;
	
	public static final int POS_BWQTY_PERUSER_POS = 16;
	public static final int POS_BWQTY_PERUSER_NEG = 17;

	
	public static final int POS_BWQTY_NSP_PRICE_POS = 18;
	public static final int POS_BWQTY_NSP_PRICE_NEG = 19;

	@Override
	public double setPrice(PriceStimulus ps) {
		double price = 0;
		
		double constant = Math.exp(genome[POS_PRICE_CONSTANT_POS]) 
				- Math.exp(genome[POS_PRICE_CONSTANT_NEG]);

		double valueEstimateTerm = Math.pow(ps.aspQuality, ps.qualityExponent);
		double valueEstimateCoef = Math.exp(genome[POS_PRICE_VALUE_TERM_POS]) - Math.exp(genome[POS_PRICE_VALUE_TERM_NEG]);
		
		price = constant + valueEstimateTerm * valueEstimateCoef;

		return price;
	}

	@Override
	public double improveQuality(QualityStimulus qs) {
		double qualityImprovement = 0;
		
		double constant = Math.exp(genome[POS_QUAL_CONSTANT_POS]) - Math.exp(genome[POS_QUAL_CONSTANT_NEG]);

		double valueEstimateTerm = Math.pow(qs.existingQuality, qs.qualityExponent);
		double valueEstimateCoef = Math.exp(genome[POS_QUAL_QUAL_POS]) - Math.exp(genome[POS_QUAL_QUAL_NEG]);
		
		qualityImprovement = constant + valueEstimateTerm * valueEstimateCoef;
		
		return qualityImprovement;
	}

	@Override
	public double buyBandwidth(BackbonePurchaseStimulus bps) {
		
		double usersEstimatePopCoef = Math.exp(genome[POS_BWQTY_TOTALPOP_POS]) 
				- Math.exp(genome[POS_BWQTY_TOTALPOP_NEG]);
		double usersEstimateNspCustCoef = Math.exp(genome[POS_BWQTY_NSPCUSTOMERS_POS]) 
				- Math.exp(genome[POS_BWQTY_NSPCUSTOMERS_NEG]);
		double usersEstimateAspCustCoef = Math.exp(genome[POS_BWQTY_ASPCUSTOMERS_POS]) 
				- Math.exp(genome[POS_BWQTY_ASPCUSTOMERS_NEG]);
		double usersEstimateIntersectionCoef = Math.exp(genome[POS_BWQTY_INTERSECTIONCUSTOMERS_POS]) 
				- Math.exp(genome[POS_BWQTY_INTERSECTIONCUSTOMERS_NEG]);
		
		double usersEstimate = bps.totalPopulation * usersEstimatePopCoef 
				+ bps.nspCustomers * usersEstimateNspCustCoef
				+ bps.aspCustomers * usersEstimateAspCustCoef
				+ bps.intersectionCustomers * usersEstimateIntersectionCoef;
		
		double beforePrice = usersEstimate * (Math.exp(genome[POS_BWQTY_PERUSER_POS]) 
				- Math.exp(genome[POS_BWQTY_PERUSER_NEG]));
		
		double priceTerm = (Math.exp(genome[POS_BWQTY_PERUSER_POS]) 
				- Math.exp(genome[POS_BWQTY_PERUSER_NEG])) * bps.price;
		
		
		double bandwidth = beforePrice + priceTerm;
		
		return bandwidth;
	}
}
