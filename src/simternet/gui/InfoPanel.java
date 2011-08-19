package simternet.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import simternet.Simternet;

/**
 * Sets up a JPanel that displays information about the current displayed
 * Simternet.
 * 
 * @author graysonwright
 * 
 */
public class InfoPanel extends JPanel {

	protected JLabel			chunkLabel;
	protected JLabel			generationLabel;
	protected Simternet			sim;
	protected JLabel			stepLabel;

	// the number of columns is just the number of information pieces we're
	// displaying. if you add/remove a piece of information, update this as
	// well.
	protected static final int	numCols				= 3;

	private static final long	serialVersionUID	= 1L;

	public InfoPanel(Simternet sim) {
		super();
		this.sim = sim;
		initComponents();
	}

	/**
	 * Sets up the Labels that will show the desired information, and adds them
	 * to the Panel.
	 */
	protected void initComponents() {
		setLayout(new GridLayout(2, InfoPanel.numCols));

		// Display the key labels
		this.add(new JLabel("Generation:"));
		this.add(new JLabel("Chunk:"));
		this.add(new JLabel("Step:"));

		// Display the value labels
		generationLabel = new JLabel("0");
		this.add(generationLabel);

		chunkLabel = new JLabel("0");
		this.add(chunkLabel);

		stepLabel = new JLabel("0");
		this.add(stepLabel);

		this.update();

	}

	/**
	 * changes the simternet object that this infoPanel is displaying
	 * 
	 * @param sim
	 *            the new simternet object
	 */
	public void setSimternet(Simternet sim) {
		this.sim = sim;
		this.update();
	}

	/**
	 * Update each of the labels displaying information
	 */
	public void update() {
		if (sim != null) {
			generationLabel.setText(Integer.toString(sim.generation));
			chunkLabel.setText(Integer.toString(sim.chunk));
			stepLabel.setText(Long.toString(sim.schedule.getSteps()));
		}
	}
}
