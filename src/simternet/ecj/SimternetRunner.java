package simternet.ecj;

import simternet.Simternet;

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
		while (step < this.steps) {
			this.s.schedule.step(this.s);
			step++;
		}
	}

}
