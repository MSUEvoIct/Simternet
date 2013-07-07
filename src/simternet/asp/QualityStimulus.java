package simternet.asp;

public class QualityStimulus {

	public double existingQuality;
	public double qualityExponent;
	
	public QualityStimulus(ASP asp) {
		existingQuality = asp.quality;
		qualityExponent = asp.s.qualityExponent;
	}
}
