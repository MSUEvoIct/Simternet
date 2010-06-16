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
	 * For testing...
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Exogenous foo = Exogenous.getDefaults();
		String bar = foo.getProperty("nsp.misc.names");
		String names[] = bar.split("\\W");
		for (String name : names)
			System.out.println(name);
	}

	private int aspCounter = 1;

	private int ccCounter = 1;
	private Integer debugLevel;
	private int nspCounter = 1;

	public Integer debugLevel() {
		if (null == this.debugLevel)
			this.debugLevel = Integer.parseInt(this
					.getProperty("general.debugLevel"));
		return this.debugLevel;
	}

	/**
	 * It is helpful to have more human-readable names for agents than the
	 * default toString method, which simply returns the name of the class and
	 * the address of the object as stored in memory. Additionally, this
	 * variable name will not be consistent between different simulation runs.
	 * This function simply appends a serial number to the name listed under the
	 * "nsp.misc.namePrefix" property.
	 * 
	 * @return An arbitrary name for this ApplicationServiceProvider
	 */
	public String getASPName() {
		return this.getProperty("asp.misc.namePrefix") + this.aspCounter++;
	}

	/**
	 * It is helpful to have more human-readable names for agents than the
	 * default toString method, which simply returns the name of the class and
	 * the address of the object as stored in memory. Additionally, this
	 * variable name will not be consistent between different simulation runs.
	 * This function simply appends a serial number to the name listed under the
	 * "consumers.misc.namePrefix" property.
	 * 
	 * @return An arbitrary name for this ConsumerClass
	 */
	public String getCCName() {
		return this.getProperty("consumers.misc.namePrefix") + this.ccCounter++;
	}

	/**
	 * It is helpful to have more human-readable names for agents than the
	 * default toString method, which simply returns the name of the class and
	 * the address of the object as stored in memory. Additionally, this
	 * variable name will not be consistent between different simulation runs.
	 * This function simply appends a serial number to the name listed under the
	 * "nsp.misc.namePrefix" property.
	 * 
	 * @return An arbitrary name for this NetworkServiceProvider
	 */
	public String getNSPName() {
		return this.getProperty("nsp.misc.namePrefix") + this.nspCounter++;
	}

	public void setDebugLevel(Integer debugLevel) {
		this.debugLevel = debugLevel;
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
