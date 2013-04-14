package simternet.junit;

import simternet.Simternet;
import simternet.asp.ASP;
import simternet.asp.ASPIndividual;
import ec.vector.FloatVectorIndividual;

public class UnitASPIndividual extends FloatVectorIndividual implements ASPIndividual {
	private static final long serialVersionUID = 1L;

	@Override
	public void setPrice(ASP asp, Simternet s) {
		// just set unit price; for testing purposes
		asp.price = 1;
		
	}

	@Override
	public void improveQuality(ASP asp, Simternet s) {
		// just set unit quality; for testing purposes
		asp.improveQuality(1);
		
	}

	
	
	
}
