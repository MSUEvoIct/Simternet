package simternet.gui.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.engine.Simternet;

/**
 * A property that displays a boolean value, and can track changes in the value
 * over time
 * 
 * @author graysonwright
 * 
 */
public class BooleanProperty extends TrackableProperty {

	protected HashMap<Integer, Boolean>	changes;
	protected Boolean					value;
	protected JLabel					valueLabel;

	/**
	 * Creates an (initially false) BooleanProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public BooleanProperty(String propertyName, Simternet sim) {
		this(propertyName, new Boolean(false), sim);
	}

	/**
	 * creates a BooleanProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param value
	 *            initial value for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public BooleanProperty(String propertyName, Boolean value, Simternet sim) {
		super(propertyName, sim);

		this.value = value;
		valueLabel = new JLabel(value.toString());
		this.add(valueLabel);
	}

	private static final long	serialVersionUID	= 1L;

	public Boolean getValue() {
		return value;
	}

	@Override
	public void printTrackedData() {
		if (changes != null) {
			System.out.println(propertyName);
			System.out.println(changes.toString());
		}
	}

	/**
	 * Store the given value in the map of changes, using the simternet's
	 * current step number as a key
	 * 
	 * @param value
	 */
	private void recordChange(Boolean value) {
		changes.put(getStep(), value);
	}

	public void setValue(boolean newValue) {
		this.setValue(new Boolean(newValue));
	}

	/**
	 * Update this property's value, recording the change if appropriate
	 * 
	 * @param value
	 *            the new value to set
	 */
	public void setValue(Boolean value) {
		if (tracking && value != this.value) {
			recordChange(value);
		}

		this.value = value;
		valueLabel.setText(this.value.toString());
	}

	/**
	 * Called by superclass when tracking is started
	 */
	@Override
	protected void trackingTurnedOn() {
		changes = new HashMap<Integer, Boolean>();
		recordChange(value);

	}
}
