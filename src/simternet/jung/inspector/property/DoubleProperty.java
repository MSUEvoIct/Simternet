package simternet.jung.inspector.property;

import java.util.HashMap;

import javax.swing.JLabel;

public class DoubleProperty extends TrackableProperty {

	protected HashMap<Integer, Double>	changes;
	protected Double					value;
	protected JLabel					valueLabel;
	private static final long			serialVersionUID	= 1L;

	public DoubleProperty(String propertyName) {
		this(propertyName, new Double(0));
	}

	public DoubleProperty(String propertyName, Double value) {
		super(propertyName);

		this.value = value;
		this.valueLabel = new JLabel(this.value.toString());
		this.add(this.valueLabel);
	}

	public Double getValue() {
		return this.value;
	}

	@Override
	public void printTrackedData() {
		// TODO Auto-generated method stub
		if (this.changes != null)
			System.out.println(this.changes.toString());
	}

	private void recordValue(Double value) {
		this.changes.put(this.getStep(), value);
	}

	public void setValue(double value) {
		this.setValue(new Double(value));
	}

	public void setValue(Double value) {

		if (this.tracking && (value != this.value))
			this.recordValue(value);

		this.value = value;
		this.valueLabel.setText(this.value.toString());
	}

	@Override
	protected void trackingStateChanged() {
		if (this.tracking) {
			this.changes = new HashMap<Integer, Double>();
			this.recordValue(this.value);
		}
	}
}
