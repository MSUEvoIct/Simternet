package simternet.data.output;

import java.io.PrintStream;

import sim.engine.SimState;

public class TestReporter extends Reporter {
	private transient PrintStream	out;

	public TestReporter(PrintStream testPS, int interval) {
		super(interval);
		out = testPS;
	}

	@Override
	public void collectData(SimState state) {
		out.println("Hello from reporter for gen, chunk, step = " + generation + "," + chunk + "," + step);
	}

	@Override
	public String getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSpecificHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}