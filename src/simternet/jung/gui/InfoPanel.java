package simternet.jung.gui;

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

	protected static final int	numCols				= 3;

	private static final long	serialVersionUID	= 1L;

	public InfoPanel(Simternet sim) {
		super();
		this.sim = sim;
		this.initComponents();
	}

	public Simternet getSim() {
		return this.sim;
	}

	/**
	 * Sets up the Labels that will show the desired information, and adds them
	 * to the Panel.
	 */
	protected void initComponents() {
		this.setLayout(new GridLayout(2, InfoPanel.numCols));

		// Display the key labels
		this.add(new JLabel("Generation:"));
		this.add(new JLabel("Chunk:"));
		this.add(new JLabel("Step:"));

		// Display the value labels
		this.generationLabel = new JLabel("0");
		this.add(this.generationLabel);

		this.chunkLabel = new JLabel("0");
		this.add(this.chunkLabel);

		this.stepLabel = new JLabel("0");
		this.add(this.stepLabel);

		this.update();

	}

	public void setSimternet(Simternet sim) {
		this.sim = sim;
		this.update();
	}

	public void update() {
		this.generationLabel.setText(Integer.toString(this.sim.generation));
		this.chunkLabel.setText(Integer.toString(this.sim.chunk));
		this.stepLabel.setText(Long.toString(this.sim.schedule.getSteps()));
	}
}
