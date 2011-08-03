package simternet.jung.inspector.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import simternet.Simternet;

public abstract class TrackableProperty extends Property {

	protected boolean			tracking;
	protected JCheckBox			trackingCheckBox;
	private static final long	serialVersionUID	= 1L;
	protected static Simternet	sim;

	/**
	 * Gives Property objects access to the Simternet object in which they're
	 * running. This will give them access to the schedule, so that when they
	 * track changes, they can have a record of when the changes happened.
	 * 
	 * If you don't call this when you're starting up, you won't be able to
	 * track changes through the Property class.
	 * 
	 * TODO: Improve this mechanism
	 * 
	 * @param sim
	 */
	public static void setSimState(Simternet sim) {
		TrackableProperty.sim = sim;
	}

	public TrackableProperty(String propertyName) {
		super(propertyName);
	}

	@Override
	protected void initComponents(String propertyName) {
		super.initComponents(propertyName);

		this.tracking = false;
		this.trackingCheckBox = new JCheckBox();
		this.trackingCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TrackableProperty.this.trackingCheckBoxClicked();
			}
		});
		this.add(this.trackingCheckBox);
	}

	public abstract void printTrackedData();

	protected void trackingCheckBoxClicked() {
		this.tracking = this.trackingCheckBox.isSelected();
		this.trackingStateChanged();
	}

	protected abstract void trackingStateChanged();
}
