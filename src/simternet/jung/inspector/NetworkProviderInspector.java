package simternet.jung.inspector;

import java.awt.GridLayout;

import javax.swing.JButton;
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
public class NetworkProviderInspector extends EvolvableAgentInspector {

	protected JLabel			nameLabel;
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

		// Initialize components
		this.nameLabel = new JLabel();

		JButton button = new JButton("Print");
		button.addActionListener(this);

		// Add them to frame
		this.add(new JLabel("Name"));
		this.add(this.nameLabel);

		this.add(new JLabel("ECJ tree"));
		this.add(button);

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
		this.pack();
	}
}
