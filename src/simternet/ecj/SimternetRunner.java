package simternet.ecj;

import simternet.engine.Simternet;

public class SimternetRunner implements Runnable {

	Simternet	s;
	int			steps	= 0;

	public SimternetRunner(Simternet s, int steps) {
		this.s = s;
		this.steps = steps;
	}

	@Override
	public void run() {
		int step = 0;
		while (step < steps) {
			s.schedule.step(s);
			step++;
		}
		s.finish();
		s = null;
	}

}
