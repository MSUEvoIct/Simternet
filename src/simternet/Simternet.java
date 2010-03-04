package simternet;

import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;

public class Simternet extends SimState {

	/**
	 *  Version specification is necessary to use persisted states 
	 *  across different versions of the model as it evolves.
	 */
	private static final long serialVersionUID = 1L;

	public static final double nspEndowment = 100000;
	
	public static final double netCostPhoneArea = 1000;
	public static final double netCostPhoneUser = 1;
	
	public static final double paybackRate = 0.05;
	public static final double interestRate = 0.07;
	
	public static final int landscapeWidth = 25;
	public static final int landscapeHeight = 25;
	public static final double maxPopulation = 1000;
	
	public DoubleGrid2D population;
	
	public Simternet(long seed) {
		super(seed);
		initLandscapePopulation();
		
	}
	
	/**
	 * Seed the landscape population matrix with (for now) random populations
	 * between 0 and maxPopulation.
	 */
	private void initLandscapePopulation() {
		population = new DoubleGrid2D(landscapeWidth,landscapeHeight);
		for (int x = 0; x < landscapeWidth; x++)
			for (int y = 0; y < landscapeHeight; y++)
				population.field[x][y] = random.nextDouble();
	}
	
	
	
	
}
