package simternet.jung.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.Simternet;

public class StringProperty extends TrackableProperty {

	protected HashMap<Integer, String>	changes;
	protected String					value;
	protected JLabel					valueLabel;

	private static final long			serialVersionUID	= 1L;

	public StringProperty(Simternet sim) {
		this("", "", sim);
	}

	public StringProperty(String propertyName, Simternet sim) {
		this(propertyName, "", sim);
	}

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
		// TODO Auto-generated method stub

	}

	private void recordValue(String value) {
		changes.put(getStep(), value);
	}

	public void setValue(String value) {

		if (tracking && value != this.value) {
			recordValue(value);
		}

		this.value = value;
		valueLabel.setText(value.toString());
	}

	@Override
	protected void trackingTurnedOn() {
		if (tracking) {
			changes = new HashMap<Integer, String>();
			recordValue(value);
		}
	}
}
