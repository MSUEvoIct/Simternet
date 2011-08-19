package simternet.gui.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.Simternet;

/**
 * A property that displays an integer value, and can track changes in the value
 * over time
 * 
 * @author graysonwright
 * 
 */
public class IntegerProperty extends TrackableProperty {

	protected HashMap<Integer, Integer>	changes;
	protected Integer					value;
	protected JLabel					valueLabel;
	private static final long			serialVersionUID	= 1L;

	/**
	 * Creates an (initially 0) IntegerProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public IntegerProperty(String propertyName, Simternet sim) {
		this(propertyName, new Integer(0), sim);
	}

	/**
	 * Creates an IntegerProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param value
	 *            initial value for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public IntegerProperty(String propertyName, Integer value, Simternet sim) {
		super(propertyName, sim);

		this.value = value;
		valueLabel = new JLabel(this.value.toString());
		this.add(valueLabel);
	}

	public Integer getValue() {
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
	private void recordChange(Integer value) {
		changes.put(getStep(), value);
	}

	/**
	 * Update this property's value, recording the change if appropriate
	 * 
	 * @param value
	 *            the new value to be set
	 */
	public void setValue(Integer value) {
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
		changes = new HashMap<Integer, Integer>();
		recordChange(value);
	}
}
