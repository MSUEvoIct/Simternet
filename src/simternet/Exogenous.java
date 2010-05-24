package simternet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * This class contains all the exogenous variables used as inputs to the model.
 * Due to MASON's integrated checkpointing functionality, the same code may be
 * used to continue running different instances of the simulation, some of which
 * will likely use different values for these parameters. These values must
 * remain with the simulation instance when it is serialized, so that the same
 * values can be used when the simulation is restored from the checkpoint. For
 * example, a new network service provider may try to build its network assuming
 * a 10x10 grid, but the serialized simulation may have been initialized using a
 * 5x5 grid. Obviously this would cause... problems.
 * 
 * The Java Properties class was chosen for ease of use in terms of user editing
 * of simulation properties stored in text files.
 * 
 * @author kkoning
 * 
 */
public class Exogenous extends Properties implements Serializable {

	/**
	 * Version specification is necessary to use persisted states across
	 * different versions of the model as it evolves. If this class has changed
	 * substantially, it may no likely longer be possible to resume an old
	 * simulation using current code.
	 */
	private static final long serialVersionUID = 1L;

	// public final double closeEnoughPrice = 0.1;

	// public final PopulationDistribution defaultPopulationDistribution =
	// PopulationDistribution.RANDOM_FLAT;
	// public final double interestRate = 0.07;
	// public final int landscapeX = 3;
	// public final int landscapeY = 3;
	// public final double maxPopulation = 1000;
	// public final double maxPrice = 200;
	// public final double netCostSimpleArea = 10000;
	// public final double netCostSimpleUser = 10;

	// public final double nspEndowment = 11000;
	// public final double paybackRate = 0.05;
	// public final double proportionChange = 0.3;

	public static Exogenous getDefaults() {

		Exogenous vars = new Exogenous();

		try {
			FileInputStream fis = new FileInputStream(
					"data/configuration/default.properties");
			vars.load(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return vars;
	}

	/**
	 * Convenience method
	 * 
	 * @return the size of the landscape grid in the x dimension
	 */
	public int x() {
		return Integer.parseInt(this.getProperty("landscape.xSize"));
	}

	/**
	 * Convenience method
	 * 
	 * @return the size of the landscape grid in the y dimension
	 */
	public int y() {
		return Integer.parseInt(this.getProperty("landscape.ySize"));
	}

}
