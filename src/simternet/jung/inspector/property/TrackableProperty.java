package simternet.jung.inspector.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import simternet.Simternet;

public abstract class TrackableProperty extends Property {

	protected boolean			tracking;
	protected JCheckBox			trackingCheckBox;
	protected Simternet			sim;
	private static final long	serialVersionUID	= 1L;

	public TrackableProperty(String propertyName, Simternet sim) {
		super(propertyName);
		this.sim = sim;
	}

	protected Integer getStep() {
		return new Integer((int) sim.schedule.getSteps());
	}

	@Override
	protected void initComponents(String propertyName) {
		super.initComponents(propertyName);

		tracking = false;
		trackingCheckBox = new JCheckBox();
		trackingCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TrackableProperty.this.trackingCheckBoxClicked();
			}
		});
		this.add(trackingCheckBox);
	}

	public abstract void printTrackedData();

	protected void trackingCheckBoxClicked() {
		tracking = trackingCheckBox.isSelected();
		trackingTurnedOn();
	}

	protected abstract void trackingTurnedOn();
}
