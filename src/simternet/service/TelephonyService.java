package simternet.service;

import sim.field.grid.DoubleGrid2D;
import simternet.Exogenous;

public class TelephonyService extends AbstractService {
	protected DoubleGrid2D peopleReached;
	
	public TelephonyService() {
		peopleReached = new DoubleGrid2D(Exogenous.landscapeX, Exogenous.landscapeY, 0.0);
	}
	
}
