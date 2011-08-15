package simternet.jung.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.Simternet;

public class IntegerProperty extends TrackableProperty {

	protected HashMap<Integer, Integer>	changes;
	protected Integer					value;
	protected JLabel					valueLabel;
	private static final long			serialVersionUID	= 1L;

	public IntegerProperty(String propertyName, Simternet sim) {
		this(propertyName, new Integer(0), sim);
	}

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
		// TODO Auto-generated method stub

	}

	private void recordChange(Integer value) {
		changes.put(getStep(), value);
	}

	public void setValue(Integer value) {
		if (tracking && value != this.value) {
			recordChange(value);
		}

		this.value = value;
		valueLabel.setText(this.value.toString());
	}

	@Override
	protected void trackingTurnedOn() {
		if (tracking) {
			changes = new HashMap<Integer, Integer>();
			recordChange(value);
		}
	}
}
