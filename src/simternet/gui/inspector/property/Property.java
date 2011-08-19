package simternet.gui.inspector.property;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * An interface component that displays the value of a variable. This component
 * has no knowledge of what it's displaying, but is set by other classes.
 * Subclasses are used to display properties of specific data types
 * 
 * @author graysonwright
 * 
 */
public abstract class Property extends JPanel {

	protected String			propertyName;
	protected JLabel			propertyNameLabel;
	private static final long	serialVersionUID	= 1L;

	public Property() {
		this("");
	}

	public Property(String propertyName) {
		super();

		initComponents(propertyName);
		addComponentsToFrame();
	}

	/**
	 * Adds components to the property's display and sets the property's
	 * dimensions
	 */
	private void addComponentsToFrame() {
		this.add(propertyNameLabel);

		Dimension minSize = new Dimension(50, 20);
		Dimension prefSize = new Dimension(50, 20);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
		this.add(new Box.Filler(minSize, prefSize, maxSize));

	}

	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Initializes the property's name and namelabel
	 * 
	 * @param propertyName
	 */
	protected void initComponents(String propertyName) {
		this.propertyName = propertyName;
		propertyNameLabel = new JLabel(this.propertyName);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
		propertyNameLabel.setText(this.propertyName);
	}

}
