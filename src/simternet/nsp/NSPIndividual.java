package simternet.nsp;


public interface NSPIndividual {
	
	public boolean buildEdge(EdgeBuildingStimulus ebs);
	
	public double priceEdge(EdgePricingStimulus eps);
	
	public double priceBackboneLink(BackbonePricingStimulus bps);

}
