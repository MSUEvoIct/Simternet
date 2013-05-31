package simternet.nsp;

import ec.agency.NullIndividual;

public class UnitNSPIndividual extends NullIndividual implements NSPIndividual {
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
