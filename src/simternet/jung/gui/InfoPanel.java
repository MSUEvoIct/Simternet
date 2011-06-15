package simternet.jung.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import simternet.Simternet;

/*
 * Sets up a JPanel that displays information about the current Simternet in the display.
 */
public class InfoPanel extends JPanel {

	public JLabel				chunkLabel;
	public JLabel				generationLabel;
	public JLabel				stepLabel;

	protected static final int	numCols				= 3;

	private static final long	serialVersionUID	= 1L;

	public InfoPanel() {
		super();
		this.initComponents();
	}

	public InfoPanel(Simternet simternet) {
		this();
		this.stepLabel.setText(Long.toString(simternet.schedule.getSteps()));
		this.generationLabel.setText(Integer.toString(simternet.generation));
		this.chunkLabel.setText(Integer.toString(simternet.chunk));
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

	}
}
