package simternet.jung.inspector;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import simternet.jung.gui.GUI;

/**
 * A gui that, given an object from a Simternet simulation, displays information
 * about that object to the user
 * 
 * @author graysonwright
 * 
 */
public abstract class Inspector extends JFrame {

	protected Object			object;
	protected GUI				owner;
	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes the JFrame and defines behavior to notify the owner when the
	 * inspector is closed
	 * 
	 * @param object
	 *            the object to be inspected
	 * @param owner
	 *            the GUI that this inspector reports to, if any
	 */
	public Inspector(Object object, GUI owner) {
		super(object.toString());

		this.object = object;
		this.owner = owner;

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Inspector.this.frameClosed();
			}
		});
	}

	protected void frameClosed() {
		if (this.owner != null)
			this.owner.removeInspector(this.object);
		this.dispose();
	}

	public void setObject(Object o) {
		if (o.getClass() == this.object.getClass())
			this.object = o;
		else
			System.err.println("Incorrect Type assignment. (simternet.jung.inspector.Inspector, 56)");
	}

	/**
	 * Updates the displayed information to reflect recent changes in the
	 * Simternet simulation
	 */
	abstract public void update();

}
