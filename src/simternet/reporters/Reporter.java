package simternet.reporters;

import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;

public abstract class Reporter implements Steppable {

	public Integer				chunk				= null;

	public Integer				generation			= null;

	protected int				interval			= 1;
	public Long					step				= null;
	public static final String	commonFields		= "Generation" + Reporter.separater + "Chunk" + Reporter.separater
															+ "Step,";
	public static final String	dataFileSuffix		= ".out.csv";
	public static final String	separater			= ",";
	private static final long	serialVersionUID	= 1L;

	public Reporter() {
		this(1);
	}

	public Reporter(int i) {
		super();
		interval = i;
	}

	public abstract void collectData(SimState state);

	public Integer getChunk() {
		return chunk;
	}

	public String getFullHeader() {
		return Reporter.commonFields + getSpecificHeaders();
	}

	public Integer getGeneration() {
		return generation;
	}

	public abstract String getLogger();

	public abstract String getSpecificHeaders();

	public Long getStep() {
		return step;
	}

	public void logHeaders() {
		Logger l = Logger.getLogger(getLogger());
		l.info(getFullHeader());
	}

	/**
	 * Called by Reporter's children when outputting data. Reporter enforces
	 * output of generation, chunkLabel, and step data each line.
	 * 
	 * @param line
	 */
	public void report(String line) {
		Logger l = Logger.getLogger(getLogger());
		l.trace(getGeneration() + Reporter.separater + getChunk().toString() + Reporter.separater
				+ getStep().toString() + Reporter.separater + line);
	}

	public void setChunk(Integer chunk) {
		this.chunk = chunk;
	}

	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

	public void setInterval(int i) {
		interval = i;
	}

	@Override
	public void step(SimState state) {
		step = state.schedule.getSteps();
		if (step > 0)
			if (step % interval == 0) {
				collectData(state);
			}
	}

}
