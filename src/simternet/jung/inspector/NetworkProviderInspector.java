package simternet.jung.inspector;

import simternet.ecj.EvolvableAgent;
import simternet.jung.gui.GUI;
import simternet.jung.inspector.property.StringProperty;
import simternet.jung.inspector.property.TreeProperty;
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
	 * @param numNSPs
	 *            object to inspect
	 * @param owner
	 *            the GUI in charge of this inspector
	 */
	public NetworkProviderInspector(NetworkProvider nsp, GUI owner) {
		super(nsp.toString(), owner);
		this.nsp = nsp;

		this.name = new StringProperty("Name", nsp.getName());
		this.add(this.name);

		if (this.nsp instanceof EvolvableAgent) {
			this.trees = new TreeProperty("ECJ Trees", (EvolvableAgent) nsp);
			this.add(this.trees);
		}
		this.update();
	}

	/**
	 * Updates the displayed values with the current values in the Simternet
	 * simulation.
	 */
	@Override
	public void update() {
		this.name.setValue(this.nsp.getName());
	}
}
