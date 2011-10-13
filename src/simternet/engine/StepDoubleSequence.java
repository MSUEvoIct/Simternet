package simternet.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;

public class StepDoubleSequence implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private ArrayList<Double>	data				= new ArrayList<Double>();
	private SimState			state;

	// Statistics
	// These variables store statistics about our data
	public double				kurtosis;
	public double				max;
	public double				mean;
	public double				min;
	public double				n;
	public double				skewness;
	public double				sum;
	public double				sumSq;
	public double				variance;

	public StepDoubleSequence(SimState state) {
		this.state = state;
	}

	public void set(double value) {
		int steps = (int) state.schedule.getSteps();
		while (data.size() >= steps) {
			data.add(null);
		}
		data.set(getStep(), value);
	}

	public void increment() {
		double currentVal = get();
		set(currentVal + 1);
	}

	public void add(double value) {
		double currentVal = get();
		set(currentVal + value);
	}

	public List<Double> getData() {
		return data;
	}

	/**
	 * The default value is 0.
	 * 
	 * @return
	 */
	private final double get() {
		while (data.size() >= getStep()) {
			data.add(null);
		}
		Double value = data.get(getStep());
		if (value == null)
			return 0;
		else
			return value;
	}

	private final int getStep() {
		return (int) state.schedule.getSteps();
	}

}
