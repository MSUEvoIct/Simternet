package simternet.engine;

import java.io.File;

import sim.engine.SimState;

public class CheckpointRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String checkpointFile = args[0];
		Integer steps = Integer.parseInt(args[1]);

		File checkpoint = new File(checkpointFile);

		SimState s = SimState.readFromCheckpoint(checkpoint);

		while (steps-- > 0) {
			s.schedule.step(s);
		}

		s.finish();

		System.exit(0);

	}
}
