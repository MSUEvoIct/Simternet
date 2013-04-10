package simternet.consumer;

import java.io.Serializable;

import sim.util.Int2D;
import simternet.Simternet;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Sets all landscape cells to use the same population amount, specified in
 * <base>.<i>popSize</i>.
 * 
 * @author kkoning
 *
 */
public class FlatPopulationInitializer implements PopulationInitializer, Serializable {
	private static final long serialVersionUID = 1L;

	float populationSize;

	@Override
	public void populate(Simternet s, Consumer c) {
		c.population = new float[s.landscapeSizeX][s.landscapeSizeY];
		for (Int2D loc : s.getAllLocations()) {
			c.population[loc.x][loc.y] = populationSize;
		}
	}

	@Override
	public void setup(ParameterDatabase pd, Parameter base) {
		populationSize = pd.getFloat(base.push("popSize"), null);
	}

}
	
	
