package simternet.gui.inspector;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import simternet.Simternet;
import simternet.gui.GUI;
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
	protected GUI					owner;
	protected ArrayList<Property>	properties;
	protected Simternet				sim;
	private static final long		serialVersionUID	= 1L;

	public Inspector(String title, GUI owner, Simternet sim) {
		this(title, owner);
		this.sim = sim;
	}

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
		contentPanel = new JPanel();
		setContentPane(contentPanel);

		this.owner = owner;

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Inspector.this.frameClosed();
			}
		});

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

	protected void frameClosed() {
		if (owner != null) {
			owner.removeInspector(this);
		}
		dispose();
	}

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
