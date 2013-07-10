package simternet.asp;

import ec.vector.FloatVectorIndividual;

public class LinearASPIndividual extends FloatVectorIndividual implements
		ASPIndividual {
	private static final long serialVersionUID = 1L;

	public static final int POS_PRICE_CONSTANT = 0;
	public static final int POS_PRICE_QUAL_TERM = 1;
	public static final int POS_QUAL_CONSTANT = 2;
	public static final int POS_QUAL_COEF_QUAL = 3;
	public static final int POS_QUAL_COEF_QUALVALUE = 4;
	

	// Should probably range [-1..2]
	public static final int POS_BWQTY_TOTALPOP = 5;
	public static final int POS_BWQTY_NSPCUSTOMERS = 6;
	public static final int POS_BWQTY_ASPCUSTOMERS = 7;
	public static final int POS_BWQTY_INTERSECTIONCUSTOMERS = 8;


	@Override
	public double setPrice(PriceStimulus ps) {
		double proposedPrice = genome[POS_PRICE_CONSTANT]
				+ genome[POS_PRICE_QUAL_TERM]
				* Math.pow(ps.aspQuality, ps.qualityExponent);
		if (proposedPrice <= 0)
			return 0;
		else
			return proposedPrice;
	}

	@Override
	public double improveQuality(QualityStimulus qs) {
		double proposedQuality = genome[POS_QUAL_CONSTANT]
				+ genome[POS_QUAL_COEF_QUAL] * qs.existingQuality
				+ genome[POS_QUAL_COEF_QUALVALUE]
				* Math.pow(qs.existingQuality, qs.qualityExponent);
		if (proposedQuality <= 0)
			return 0;
		else
			return proposedQuality;
	}

	@Override
	public double buyBandwidth(BackbonePurchaseStimulus bps) {
		
		double proposedBandwidth = genome[POS_BWQTY_TOTALPOP] * bps.totalPopulation 
				+ genome[POS_BWQTY_NSPCUSTOMERS] * bps.nspCustomers
				+ genome[POS_BWQTY_ASPCUSTOMERS] * bps.aspCustomers 
				+ genome[POS_BWQTY_INTERSECTIONCUSTOMERS] * bps.intersectionCustomers;
		if (proposedBandwidth <= 0)
			return 0;
		else
			return proposedBandwidth;
	}
}
