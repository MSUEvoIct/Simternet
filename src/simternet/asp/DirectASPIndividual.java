package simternet.asp;

import ec.vector.FloatVectorIndividual;

/**
 * For future modifications, this agent should respond to stimuli with values
 * encoded directly on the genome, to the extent this is possible.
 * 
 * @author kkoning
 * 
 */
public class DirectASPIndividual extends FloatVectorIndividual implements
		ASPIndividual {
	private static final long serialVersionUID = 1L;

	public static final int POS_PRICE = 0;
	public static final int POS_QUALITY = 1;
	public static final int POS_BWQTY_POPULATION = 2;
	
	@Override
	public double setPrice(PriceStimulus ps) {
		return Math.exp(genome[POS_PRICE]);
	}

	@Override
	public double improveQuality(QualityStimulus qs) {
		return Math.exp(genome[POS_QUALITY]);
	}

	@Override
	public double buyBandwidth(BackbonePurchaseStimulus bps) {
		// Bandwidth is scaled by size of population
		return Math.exp(genome[POS_BWQTY_POPULATION]) * bps.totalPopulation; 
	}

}
