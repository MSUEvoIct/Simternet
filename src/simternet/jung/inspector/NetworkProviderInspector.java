package simternet.jung.inspector;

import java.awt.GridLayout;

import javax.swing.JLabel;

import simternet.jung.gui.GUI;
import simternet.nsp.GPNetworkProvider;
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
	// TODO: add tree visualization

	protected JLabel			nameLabel, treeLabel;

	protected static final int	numRows				= 2;

	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param nsp
	 *            object to inspect
	 * @param owner
	 *            the GUI in charge of this inspector
	 */
	public NetworkProviderInspector(NetworkProvider nsp, GUI owner) {
		super(nsp, owner);

		this.setLayout(new GridLayout(NetworkProviderInspector.numRows, 2, 20, 5));

		this.nameLabel = new JLabel();
		this.treeLabel = new JLabel();

		this.add(new JLabel("Name"));
		this.add(this.nameLabel);

		this.add(new JLabel("ECJ tree"));
		this.add(this.treeLabel);

		this.update();
	}

	/**
	 * Updates the displayed values with the current values in the Simternet
	 * simulation.
	 */
	@Override
	public void update() {

		GPNetworkProvider nsp = (GPNetworkProvider) this.object;

		this.nameLabel.setText(nsp.getName());

		// try {
		// PipedOutputStream pipeOut = new PipedOutputStream();
		// PipedInputStream pipeIn = new PipedInputStream(pipeOut);
		// DataOutput dataOut = new DataOutputStream(pipeOut);
		// DataInput dataIn = new DataInputStream(pipeIn);
		// nsp.printPricingTree(this.owner.getState(), dataOut);
		// this.treeLabel.setText(dataIn.readLine());
		// } catch (IOException e) {
		this.treeLabel.setText("Could not read tree.");
		// }

		this.pack();
	}
}
