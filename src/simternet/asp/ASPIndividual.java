package simternet.asp;


public interface ASPIndividual {

	public double setPrice(PriceStimulus ps);
	
	public double improveQuality(QualityStimulus qs);
	
	public double buyBandwidth(BackbonePurchaseStimulus bps);

}
