package simternet.gui.inspector;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import simternet.Simternet;
import simternet.gui.inspector.property.Property;
import simternet.gui.inspector.property.TrackableProperty;

/**
 * A gui that, given an object from a Simternet simulation, displays information
 * about that object to the user
 * 
 * @author graysonwright
 * 
 */
public abstract class Inspector extends JFrame {

	protected JPanel				contentPanel;
	protected ArrayList<Property>	properties;
	protected Simternet				sim;
	private static final long		serialVersionUID	= 1L;

	/**
	 * Initializes the JFrame and defines the layout
	 * 
	 * @param sim
	 *            the Simternet instance that this inspector is inspecting
	 * @param title
	 *            the title of the JFrame
	 */
	public Inspector(Simternet sim, String title) {
		super(title);

		this.sim = sim;

		contentPanel = new JPanel();
		setContentPane(contentPanel);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		properties = new ArrayList<Property>();

		contentPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	/**
	 * Allows the Inspector to keep a list of all Properties added to its Frame.
	 */
	@Override
	public Component add(Component component) {
		if (component instanceof Property) {
			properties.add((Property) component);
		}
		return super.add(component);
	}

	/**
	 * Cycles through all Properties, asking them to print their data if they
	 * are a TrackableProperty
	 */
	public void printData() {
		System.out.println(getTitle());
		for (Property p : properties)
			if (p instanceof TrackableProperty) {
				((TrackableProperty) p).printTrackedData();
			}
	}

	/**
	 * Updates the displayed information to reflect recent changes in the
	 * Simternet simulation
	 */
	abstract public void update();
}
