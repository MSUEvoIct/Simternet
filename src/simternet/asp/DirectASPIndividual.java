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

	public static final int POS_PRICE = 0;
	public static final int POS_QUALITY = 1;
	
	
	@Override
	public double setPrice(PriceStimulus ps) {
		return genome[POS_PRICE];
	}

	@Override
	public double improveQuality(QualityStimulus qs) {
		return genome[POS_QUALITY];
	}

}
