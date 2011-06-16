package simternet.jung.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;

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

	protected static final int	numRows				= 1;

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

		this.setLayout(new GridLayout(EdgeInspector.numRows, 2, 20, 5));
		//
		// this.add(new JLabel("Location"));
		// this.add(new JLabel(nsp.getLocation().toString()));
		//
		// this.add(new JLabel("Owner"));
		// this.add(new JLabel(nsp.getOwner().toString()));
		//
		// this.add(new JLabel("Max Bandwidth"));
		// this.add(new JLabel(nsp.getMaxBandwidth().toString()));
		//
		// this.add(new JLabel("Price"));
		// this.add(new JLabel(nsp.getPrice().toString()));

		this.add(new JLabel("Place"));
		this.add(new JLabel("Holder"));
	}

	/**
	 * Updates the displayed values with the current values in the Simternet
	 * simulation.
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
