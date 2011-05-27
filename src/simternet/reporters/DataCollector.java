package simternet.reporters;

import sim.engine.SimState;
import sim.engine.Steppable;

public class DataCollector implements Steppable {
	public static final String	dataFileSuffix		= ".out.csv";
	public static final Integer	generation			= null;
	public static final String	outputPrefix		= "Generation,Chunk,Step";

	private static final long	serialVersionUID	= 1L;

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub

	}

}
