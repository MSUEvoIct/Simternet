package simternet.jung.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

public class StringProperty extends TrackableProperty {

	protected HashMap<Integer, String>	changes;
	protected String					value;
	protected JLabel					valueLabel;

	private static final long			serialVersionUID	= 1L;

	public StringProperty() {
		this("", "");
	}

	public StringProperty(String propertyName) {
		this(propertyName, "");
	}

	public StringProperty(String propertyName, String value) {
		super(propertyName);

		this.value = value;
		this.valueLabel = new JLabel(this.value);
		this.add(this.valueLabel);
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public void printTrackedData() {
		// TODO Auto-generated method stub

	}

	private void recordValue(String value) {
		Integer step = new Integer((int) TrackableProperty.sim.schedule.getSteps());
		this.changes.put(step, value);
	}

	public void setValue(String value) {

		if (this.tracking && (value != this.value))
			this.recordValue(value);

		this.value = value;
		this.valueLabel.setText(value.toString());
	}

	@Override
	protected void trackingStateChanged() {
		if (this.tracking) {
			this.changes = new HashMap<Integer, String>();
			this.recordValue(this.value);
		}
	}
}
