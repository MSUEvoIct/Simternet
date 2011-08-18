package simternet.gui.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.Simternet;

public class DoubleProperty extends TrackableProperty {

	protected HashMap<Integer, Double>	changes;
	protected Double					value;
	protected JLabel					valueLabel;
	private static final long			serialVersionUID	= 1L;

	public DoubleProperty(String propertyName, Simternet sim) {
		this(propertyName, new Double(0), sim);
	}

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

	private void recordValue(Double value) {
		changes.put(getStep(), value);
	}

	public void setValue(double value) {
		this.setValue(new Double(value));
	}

	public void setValue(Double value) {

		if (tracking && value != this.value) {
			recordValue(value);
		}

		this.value = value;
		valueLabel.setText(this.value.toString());
	}

	@Override
	protected void trackingTurnedOn() {
		if (tracking) {
			changes = new HashMap<Integer, Double>();
			recordValue(value);
		}
	}
}
