package simternet.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import simternet.Simternet;

/**
 * A JPanel that provides mechanisms for controlling the current Simternet
 * simulation
 * 
 * @author graysonwright
 */
public class ControlPanel extends JPanel {

	protected File				checkpointFile;
	protected GUI				owner;
	protected JSpinner			stepSelector;

	protected static final int	numRows				= 1;

	private static final long	serialVersionUID	= 1L;

	public ControlPanel(GUI owner) {
		super();
		this.owner = owner;
		this.initComponents();
	}

	protected void chooseFile() {

		JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(new JFrame("Open"));

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			this.loadFile(file);
			this.checkpointFile = file;
		}
	}

	/**
	 * Sets up the buttons and controls, and adds them to the panel
	 */
	protected void initComponents() {

		// Place controls vertically in a single column
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Add a button to load a new Simternet checkpoint
		JButton loadButton = new JButton("Load From Checkpoint");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ControlPanel.this.chooseFile();
			}
		});
		this.add(loadButton);

		// Add a button to restart the current Simternet checkpoint
		JButton restartButton = new JButton("Restart Current Checkpoint");
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ControlPanel.this.restart();
			}
		});
		this.add(restartButton);

		// Add a button to step the current Simternet
		// Along with a JSpinner that selects how many steps.
		JButton stepButton = new JButton("Step:");
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ControlPanel.this.step();
			}
		});
		this.add(stepButton);

		// set spinner's initial value:1, minimum=0, maximum=100, step:1
		this.stepSelector = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
		this.stepSelector.setMaximumSize(this.stepSelector.getPreferredSize());
		this.stepSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(this.stepSelector);

		// A button to open the filter gui
		JButton filterButton = new JButton("Modify Filters");
		filterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ControlPanel.this.owner.filterButtonPressed();
			}
		});
		this.add(filterButton);

		JButton printDataButton = new JButton("View Data");
		printDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ControlPanel.this.owner.printDataButtonPressed();
			}
		});
		this.add(printDataButton);

		// We want empty space to be at the bottom of the panel
		this.add(Box.createVerticalGlue());

		JButton nspInspectorButton = new JButton("Inspect NSPs");
		nspInspectorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ControlPanel.this.owner.NSPInspectorButtonPressed();
			}
		});
		this.add(nspInspectorButton);

		JButton aspInspectorButton = new JButton("Inspect ASPs");
		aspInspectorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ControlPanel.this.owner.ASPInspectorButtonPressed();
			}
		});
		this.add(aspInspectorButton);

		JButton edgeInspectorButton = new JButton("Inspect Edges");
		edgeInspectorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ControlPanel.this.owner.EdgeInspectorButtonPressed();
			}
		});
		this.add(edgeInspectorButton);
	}

	/**
	 * Tries to read a file, in search of a serialized Simternet object. Sends
	 * the result to its GUI owner.
	 * 
	 * @param file
	 */
	protected void loadFile(File file) {
		try {
			FileInputStream fileStream = new FileInputStream(file);
			GZIPInputStream gStream = new GZIPInputStream(fileStream);

			ObjectInputStream objectStream = new ObjectInputStream(gStream);
			this.owner.setSimternet((Simternet) objectStream.readObject());
			objectStream.close();
		} catch (Exception exception) {
			System.err.println("Unable to read from file");
			exception.printStackTrace();
		}
	}

	/**
	 * Called on a button press event
	 * 
	 * Re-loads the file that the current simulation was stored in.
	 * 
	 * Result: the current simulation gets set back to step = 0
	 */
	protected void restart() {
		this.loadFile(this.checkpointFile);
	}

	/**
	 * Called on a button press event
	 * 
	 * Tells the GUI owner of this panel to step the current simulation a given
	 * number of times
	 */
	protected void step() {
		int n = ((Integer) this.stepSelector.getValue()).intValue();
		this.owner.step(n);
	}
}
