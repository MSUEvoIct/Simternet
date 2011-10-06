package simternet.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;

public class StepBooleanSequence implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private ArrayList<Boolean>	data				= new ArrayList<Boolean>();
	private SimState			state;

	public StepBooleanSequence(SimState state) {
		this.state = state;
	}

	public void set(boolean value) {
		// make sure the array is big enough
		while (data.size() >= getStep()) {
			data.add(null);
		}
		data.set(getStep(), value);
	}

	private final boolean get() {
		while (data.size() >= getStep()) {
			data.add(null);
		}
		Boolean value = data.get(getStep());
		if (value == null)
			return false;
		else
			return value.booleanValue();
	}

	public List<Boolean> getData() {
		return data;
	}

	private final int getStep() {
		return (int) state.schedule.getSteps();
	}
}
