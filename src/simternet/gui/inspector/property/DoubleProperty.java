package simternet.gui.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.engine.Simternet;

/**
 * A property that displays a double value, and can track changes in the value
 * over time
 * 
 * @author graysonwright
 * 
 */
public class DoubleProperty extends TrackableProperty {

	protected HashMap<Integer, Double>	changes;
	protected Double					value;
	protected JLabel					valueLabel;
	private static final long			serialVersionUID	= 1L;

	/**
	 * Creates an (initially 0) DoubleProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public DoubleProperty(String propertyName, Simternet sim) {
		this(propertyName, new Double(0), sim);
	}

	/**
	 * Creates a DoubleProperty
	 * 
	 * @param propertyName
	 *            name to display for the property
	 * @param value
	 *            initial value for the property
	 * @param sim
	 *            simternet object that the property exists in
	 */
	public DoubleProperty(String propertyName, Double value, Simternet sim) {
		super(propertyName, sim);

		this.value = value;
		valueLabel = new JLabel(this.value.toString());
		this.add(valueLabel);
	}

	public Double getValue() {
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
	private void recordChange(Double value) {
		changes.put(getStep(), value);
	}

	public void setValue(double value) {
		this.setValue(new Double(value));
	}

	/**
	 * Update this property's value, recording the change if appropriate
	 * 
	 * @param value
	 *            the new value to be set
	 */
	public void setValue(Double value) {

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
		changes = new HashMap<Integer, Double>();
		recordChange(value);
	}
}
