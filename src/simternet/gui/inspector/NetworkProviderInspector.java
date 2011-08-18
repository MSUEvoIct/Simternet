package simternet.gui.inspector;

import simternet.ecj.EvolvableAgent;
import simternet.gui.inspector.property.StringProperty;
import simternet.gui.inspector.property.TreeProperty;
import simternet.nsp.NetworkProvider;

/**
 * Inspects NetworkProvider objects
 * 
 * Currently displays no information. TODO: Add information to display
 * 
 * @author graysonwright
 * 
 */
public class NetworkProviderInspector extends Inspector {

	protected StringProperty	name;
	protected NetworkProvider	nsp;
	protected TreeProperty		trees;
	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param nsp
	 *            object to inspect
	 */
	public NetworkProviderInspector(NetworkProvider nsp) {
		super(nsp.s, nsp.toString());
		this.nsp = nsp;

		name = new StringProperty("Name", nsp.getName(), sim);
		this.add(name);

		if (this.nsp instanceof EvolvableAgent) {
			trees = new TreeProperty("ECJ Trees", (EvolvableAgent) nsp);
			this.add(trees);
		}
		this.update();
	}

	/**
	 * Updates the displayed values with the current values in the Simternet
	 * simulation.
	 */
	@Override
	public void update() {
		name.setValue(nsp.getName());
	}
}