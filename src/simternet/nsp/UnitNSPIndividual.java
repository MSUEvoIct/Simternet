package simternet.nsp;

import ec.vector.FloatVectorIndividual;

public class UnitNSPIndividual extends FloatVectorIndividual implements NSPIndividual {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean buildEdge(EdgeBuildingStimulus ebs) {
		return true;
	}

	@Override
	public double priceEdge(EdgePricingStimulus eps) {
		return 1;
	}

	@Override
	public double priceBackboneLink(BackbonePricingStimulus bps) {
		return 1;
	}

	
	
	
}
