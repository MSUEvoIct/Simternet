package simternet.gui.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

import simternet.Simternet;

public class BooleanProperty extends TrackableProperty {

	protected HashMap<Integer, Boolean>	changes;
	protected Boolean					value;
	protected JLabel					valueLabel;

	public BooleanProperty(String propertyName, Simternet sim) {
		this(propertyName, new Boolean(false), sim);
	}

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

	private void recordChange(Boolean value) {
		changes.put(getStep(), value);
	}

	public void setValue(boolean newValue) {
		this.setValue(new Boolean(newValue));
	}

	public void setValue(Boolean value) {
		if (tracking && value != this.value) {
			recordChange(value);
		}

		this.value = value;
		valueLabel.setText(this.value.toString());
	}

	@Override
	protected void trackingTurnedOn() {
		if (tracking) {
			changes = new HashMap<Integer, Boolean>();
			recordChange(value);
		}
	}
}
