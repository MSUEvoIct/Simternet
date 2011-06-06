package simternet.reporters;

import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;

public abstract class Reporter implements Steppable {

	public Integer				chunk				= null;

	public Integer				generation			= null;
	protected int				interval			= 1;
	public Long					step				= null;
	public static final String	commonFields		= "Generation,Chunk,Step,";
	public static final String	dataFileSuffix		= ".out.csv";
	public static final String	separater			= ",";
	private static final long	serialVersionUID	= 1L;

	public Reporter() {
		super();
	}

	public Reporter(int i) {
		super();
		this.interval = i;
	}

	public abstract void collectData(SimState state);

	public Integer getChunk() {
		return this.chunk;
	}

	public String getFullHeader() {
		return Reporter.commonFields + this.getSpecificHeaders();
	}

	public Integer getGeneration() {
		return this.generation;
	}

	public abstract String getLogger();

	public abstract String getSpecificHeaders();

	public Long getStep() {
		return this.step;
	}

	public void logHeaders() {
		Logger l = Logger.getLogger(this.getLogger());
		l.info(this.getFullHeader());
	}

	/**
	 * Called by Reporter's children when outputting data. Reporter enforces
	 * output of generation, chunk, and step data each line.
	 * 
	 * @param line
	 */
	public void report(String line) {
		Logger l = Logger.getLogger(this.getLogger());
		l.info(this.getGeneration() + Reporter.separater + this.getChunk().toString() + Reporter.separater
				+ this.getStep().toString() + Reporter.separater + line);
	}

	public void setChunk(Integer chunk) {
		this.chunk = chunk;
	}

	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

	public void setInterval(int i) {
		this.interval = i;
	}

	@Override
	public void step(SimState state) {
		this.step = state.schedule.getSteps();
		if (this.step % this.interval == 0)
			this.collectData(state);
	}

}
