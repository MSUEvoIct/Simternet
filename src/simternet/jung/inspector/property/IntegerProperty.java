package simternet.jung.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

public class IntegerProperty extends TrackableProperty {

	protected HashMap<Integer, Integer>	changes;
	protected Integer					value;
	protected JLabel					valueLabel;
	private static final long			serialVersionUID	= 1L;

	public IntegerProperty(String propertyName) {
		this(propertyName, new Integer(0));
	}

	public IntegerProperty(String propertyName, Integer value) {
		super(propertyName);

		this.value = value;
		this.valueLabel = new JLabel(this.value.toString());
		this.add(this.valueLabel);
	}

	public Integer getValue() {
		return this.value;
	}

	@Override
	public void printTrackedData() {
		// TODO Auto-generated method stub

	}

	private void recordChange(Integer value) {
		Integer step = (int) TrackableProperty.sim.schedule.getSteps();
		this.changes.put(step, value);
	}

	public void setValue(Integer value) {
		if (this.tracking && (value != this.value))
			this.recordChange(value);

		this.value = value;
		this.valueLabel.setText(this.value.toString());
	}

	@Override
	protected void trackingStateChanged() {
		if (this.tracking) {
			this.changes = new HashMap<Integer, Integer>();
			this.recordChange(this.value);
		}
	}
}
