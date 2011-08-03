package simternet.jung.inspector;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import simternet.jung.gui.GUI;
import simternet.jung.inspector.property.Property;
import simternet.jung.inspector.property.TrackableProperty;

/**
 * A gui that, given an object from a Simternet simulation, displays information
 * about that object to the user
 * 
 * @author graysonwright
 * 
 */
public abstract class Inspector extends JFrame {

	protected JPanel				contentPanel;
	protected GUI					owner;
	protected ArrayList<Property>	properties;
	private static final long		serialVersionUID	= 1L;

	/**
	 * Initializes the JFrame and defines behavior to notify the owner when the
	 * inspector is closed
	 * 
	 * @param title
	 *            the title of the JFrame
	 * @param owner
	 *            the GUI that this inspector reports to, if any
	 */
	public Inspector(String title, GUI owner) {
		super(title);
		this.contentPanel = new JPanel();
		this.setContentPane(this.contentPanel);

		this.owner = owner;

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Inspector.this.frameClosed();
			}
		});

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.properties = new ArrayList<Property>();

		this.contentPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	/**
	 * Allows the Inspector to keep a list of all Properties added to its Frame.
	 */
	@Override
	public Component add(Component component) {
		if (component instanceof Property)
			this.properties.add((Property) component);
		return super.add(component);
	}

	protected void frameClosed() {
		if (this.owner != null)
			this.owner.removeInspector(this);
		this.dispose();
	}

	public void printData() {
		for (Property p : this.properties)
			if (p instanceof TrackableProperty)
				((TrackableProperty) p).printTrackedData();
	}

	/**
	 * Updates the displayed information to reflect recent changes in the
	 * Simternet simulation
	 */
	abstract public void update();
}
