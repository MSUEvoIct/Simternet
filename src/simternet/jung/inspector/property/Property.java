package simternet.jung.inspector.property;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class Property extends JPanel {

	protected String			propertyName;
	protected JLabel			propertyNameLabel;
	private static final long	serialVersionUID	= 1L;

	public Property() {
		this("");
	}

	public Property(String propertyName) {
		super();

		this.initComponents(propertyName);
		this.addComponentsToFrame();
	}

	private void addComponentsToFrame() {
		this.add(this.propertyNameLabel);

		Dimension minSize = new Dimension(50, 20);
		Dimension prefSize = new Dimension(50, 20);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
		this.add(new Box.Filler(minSize, prefSize, maxSize));

	}

	public String getPropertyName() {
		return this.propertyName;
	}

	protected void initComponents(String propertyName) {
		this.propertyName = propertyName;
		this.propertyNameLabel = new JLabel(this.propertyName);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
		this.propertyNameLabel.setText(this.propertyName);
	}

}
