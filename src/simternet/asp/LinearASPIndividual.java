package simternet.asp;

import ec.vector.FloatVectorIndividual;

public class LinearASPIndividual extends FloatVectorIndividual implements
		ASPIndividual {
	private static final long serialVersionUID = 1L;

	// Pricing
	public static final int POS_PRICE_CONSTANT = 0;
	public static final int POS_PRICE_QUAL_TERM = 1;

	// Investment in Quality
	public static final int POS_QUAL_CONSTANT = 2;

	// Purchasing of backbone bandwidth
	// Should probably range [-1..2]
	public static final int POS_BWQTY_TOTALPOP = 3;
	public static final int POS_BWQTY_NSPCUSTOMERS = 4;
	public static final int POS_BWQTY_ASPCUSTOMERS = 5;
	public static final int POS_BWQTY_INTERSECTIONCUSTOMERS = 6;
	public static final int POS_BWQTY_NSP_PRICE = 7;

	@Override
	public double setPrice(PriceStimulus ps) {
		double price = genome[POS_PRICE_CONSTANT] + genome[POS_PRICE_QUAL_TERM]
				* Math.pow(ps.aspQuality, ps.qualityExponent);
		return price;
	}

	@Override
	public double improveQuality(QualityStimulus qs) {
		double quality = genome[POS_QUAL_CONSTANT];
		return quality;
	}

	@Override
	public double buyBandwidth(BackbonePurchaseStimulus bps) {

		double bandwidth = (genome[POS_BWQTY_TOTALPOP] * bps.totalPopulation
				+ genome[POS_BWQTY_NSPCUSTOMERS] * bps.nspCustomers
				+ genome[POS_BWQTY_ASPCUSTOMERS] * bps.aspCustomers
				+ genome[POS_BWQTY_INTERSECTIONCUSTOMERS]
				* bps.intersectionCustomers) * bps.price * (-Math.exp(genome[POS_BWQTY_NSP_PRICE])) ;
		return bandwidth;
	}
}
