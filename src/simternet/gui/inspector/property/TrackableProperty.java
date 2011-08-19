package simternet.gui.inspector.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import simternet.Simternet;

/**
 * A type of Property that tracks changes to its value over time. Subclasses
 * must provide a mechanism for tracking the changes, and be able to print the
 * changes out on request.
 * 
 * @author graysonwright
 * 
 */
public abstract class TrackableProperty extends Property {

	protected boolean			tracking;
	protected JCheckBox			trackingCheckBox;
	protected Simternet			sim;
	private static final long	serialVersionUID	= 1L;

	public TrackableProperty(String propertyName, Simternet sim) {
		super(propertyName);
		this.sim = sim;
	}

	/**
	 * Gets the number of steps the Simternet model has gone through.
	 * 
	 * @return the number of steps
	 */
	protected Integer getStep() {
		return new Integer((int) sim.schedule.getSteps());
	}

	/**
	 * In addition to the superclass's components, initializes a checkbox that
	 * allows the user to specify when to track the data.
	 */
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
		// add the checkbox before the property Name Label.
		this.add(trackingCheckBox);
	}

	public abstract void printTrackedData();

	/**
	 * Called when the user toggles the trackingCheckBox. Notifies subclass to
	 * start tracking changes.
	 */
	protected void trackingCheckBoxClicked() {
		tracking = trackingCheckBox.isSelected();
		if (tracking) {
			trackingTurnedOn();
		}
	}

	protected abstract void trackingTurnedOn();
}
