package simternet.asp;

import ec.agency.NullIndividual;

/**
 * Used for testing purposes, these individuals should always make the simplest
 * response to any stimuli.  For example, if choosing a real value, always use
 * 1.0.
 * 
 * @author kkoning
 *
 */
public class UnitASPIndividual extends NullIndividual implements ASPIndividual {
	private static final long serialVersionUID = 1L;

	@Override
	public double setPrice(PriceStimulus ps) {
		return 1;
	}

	@Override
	public double improveQuality(QualityStimulus qs) {
		return 1;
	}

	@Override
	public double buyBandwidth(BackbonePurchaseStimulus bps) {
		return 1;
	}
	
}
