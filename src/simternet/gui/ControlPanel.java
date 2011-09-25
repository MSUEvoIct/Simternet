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

import simternet.engine.Simternet;

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
		initComponents();
	}

	/**
	 * Called when the user clicks the "Load From Checkpoint" button. Opens a
	 * file chooser dialog box and lets the user pick a file, then loads it.
	 */
	protected void chooseFile() {
		JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(new JFrame("Open"));

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			loadFile(file);
			checkpointFile = file;
		}
	}

	/**
	 * Sets up buttons and controls, and adds them to the panel
	 */
	protected void initComponents() {

		// Place controls vertically in a single column
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Add a button to load a new Simternet checkpoint
		// calls function chooseFile()
		JButton loadButton = new JButton("Load From Checkpoint");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ControlPanel.this.chooseFile();
			}
		});
		this.add(loadButton);

		// Add a button to restart the current Simternet checkpoint
		// calls function restart()
		JButton restartButton = new JButton("Restart Current Checkpoint");
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ControlPanel.this.restart();
			}
		});
		this.add(restartButton);

		// Add a button to step the current Simternet, along with a JSpinner
		// that selects how many steps.
		// calls function step()
		JButton stepButton = new JButton("Step:");
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ControlPanel.this.step();
			}
		});
		this.add(stepButton);

		// set spinner's initial value:1, minimum=0, maximum=100, step:1
		stepSelector = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
		stepSelector.setMaximumSize(stepSelector.getPreferredSize());
		stepSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(stepSelector);

		// A button to open the filter gui
		// calls function filterButtonPressed() in class GUI
		JButton filterButton = new JButton("Modify Filters");
		filterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				owner.filterButtonPressed();
			}
		});
		this.add(filterButton);

		// A button to print any data recorded by inspectors.
		// calls function printDataButtonPressed() in class GUI
		JButton printDataButton = new JButton("View Data");
		printDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				owner.printDataButtonPressed();
			}
		});
		this.add(printDataButton);

		// Create separation for esthetic purposes
		this.add(Box.createVerticalGlue());

		// Add button that triggers a global NSP inspector
		// calls function nspInspectorButtonPressed() in class GUI
		JButton nspInspectorButton = new JButton("Inspect NSPs");
		nspInspectorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				owner.nspInspectorButtonPressed();
			}
		});
		this.add(nspInspectorButton);

		// Add button that triggers a global ASP inspector
		// calls function aspInspectorButtonPressed() in class GUI
		JButton aspInspectorButton = new JButton("Inspect ASPs");
		aspInspectorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				owner.aspInspectorButtonPressed();
			}
		});
		this.add(aspInspectorButton);

		// Add button that triggers a global edge inspector
		// calls function edgeInspectorButtonPressed() in class GUI
		JButton edgeInspectorButton = new JButton("Inspect Edges");
		edgeInspectorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				owner.edgeInspectorButtonPressed();
			}
		});
		this.add(edgeInspectorButton);
	}

	/**
	 * Utility method. Tries to read a file, in search of a serialized Simternet
	 * object. Sends the result to its GUI owner.
	 * 
	 * @param file
	 */
	protected void loadFile(File file) {
		try {
			FileInputStream fileStream = new FileInputStream(file);
			// serialized simternet objects are gzipped.
			GZIPInputStream gStream = new GZIPInputStream(fileStream);

			ObjectInputStream objectStream = new ObjectInputStream(gStream);
			owner.setSimternet((Simternet) objectStream.readObject());
			objectStream.close();
		} catch (Exception exception) {
			System.err.println("Unable to read from file");
			exception.printStackTrace();
		}
	}

	/**
	 * Called when user presses "Restart Current Checkpoint" button. Re-loads
	 * the file that the current simulation was stored in.
	 * 
	 * Result: the current simulation gets set back to step = 0
	 */
	protected void restart() {
		if (owner.hasValidSimternetInstance()) {
			loadFile(checkpointFile);
		} else {
			System.err.println("Cannot restart Simternet because no serialized instance is currently loaded.");
		}
	}

	/**
	 * Called when user pressed "Step:" button.
	 * 
	 * Tells the GUI owner of this panel to step the current simulation a given
	 * number of times, based on the value of the slider.
	 */
	protected void step() {
		if (owner.hasValidSimternetInstance()) {
			int n = ((Integer) stepSelector.getValue()).intValue();
			owner.step(n);
		} else {
			System.err.println("Cannot step Simternet because no serialized instance is currently loaded.");
		}
	}
}
