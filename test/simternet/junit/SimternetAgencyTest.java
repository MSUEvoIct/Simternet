package simternet.junit;

import org.junit.Test;

import ec.EvolutionState;

/**
 * Simternet should be able to be run through the ECJ/Agency framework.
 * 
 * @author kkoning
 * 
 */
public class SimternetAgencyTest {

	@Test
	public void test() {
		String[] args = new String[2];
		args[0] = "-file";
		args[1] = "test/simternet/junit/simternet.test.properties";

		EvolveTest.main(args);

	}

	/**
	 * A simple overide of the ECJ main() function is required for testing
	 * purposes because the standard function issues an explicit 
	 * System.exit(0); at the end.
	 * 
	 * @author kkoning
	 *
	 */
	public static class EvolveTest extends ec.Evolve {

		public static void main(String[] args) {
			EvolutionState state = possiblyRestoreFromCheckpoint(args);
			if (state != null) // loaded from checkpoint
				state.run(EvolutionState.C_STARTED_FROM_CHECKPOINT);
			else {
				state = initialize(loadParameterDatabase(args), 0);
				state.run(EvolutionState.C_STARTED_FRESH);
			}
			cleanup(state);
		}

	}

}
