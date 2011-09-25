package simternet.gui.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.engine.Simternet;

/**
 * A property that displays a string value, and can track changes in the value
 * over time
 * 
 * @author graysonwright
 * 
 */
public class StringProperty extends TrackableProperty {

	protected HashMap<Integer, String>	changes;
	protected String					value;
	protected JLabel					valueLabel;

	private static final long			serialVersionUID	= 1L;

	/**
	 * Creates an (initially empty, nameless) StringProperty
	 * 
	 * @param sim
	 *            the simternet object that the property exists in
	 */
	public StringProperty(Simternet sim) {
		this("", "", sim);
	}

	/**
	 * Creates an (initially empty) StringProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public StringProperty(String propertyName, Simternet sim) {
		this(propertyName, "", sim);
	}

	/**
	 * Creates a StringProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param value
	 *            initial value for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public StringProperty(String propertyName, String value, Simternet sim) {
		super(propertyName, sim);

		this.value = value;
		valueLabel = new JLabel(this.value);
		this.add(valueLabel);
	}

	public String getValue() {
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
	private void recordChange(String value) {
		changes.put(getStep(), value);
	}

	/**
	 * Update this property's value, recording the change if appropriate
	 * 
	 * @param value
	 *            the new value to be set
	 */
	public void setValue(String value) {

		if (tracking && value != this.value) {
			recordChange(value);
		}

		this.value = value;
		valueLabel.setText(value.toString());
	}

	/**
	 * Called by the superclass when tracking is started
	 */
	@Override
	protected void trackingTurnedOn() {
		if (tracking) {
			changes = new HashMap<Integer, String>();
			recordChange(value);
		}
	}
}
