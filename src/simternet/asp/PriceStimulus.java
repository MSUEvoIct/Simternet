package simternet.asp;

public class PriceStimulus {

	public double numASPCustomers;
	public double qualityExponent;
	public double aspQuality;
	
	public PriceStimulus(ASP asp) {
		this.numASPCustomers = asp.getCustomers();
		aspQuality = asp.quality;
		qualityExponent = asp.s.qualityExponent;
	}
	
}
