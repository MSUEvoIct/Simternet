package simternet.service;

import java.io.Serializable;

import sim.field.grid.DoubleGrid2D;
import simternet.Exogenous;

public class TelephonyService extends AbstractService implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DoubleGrid2D peopleReached;

	public TelephonyService() {
		this.peopleReached = new DoubleGrid2D(Exogenous.landscapeX,
				Exogenous.landscapeY, 0.0);
	}

}
