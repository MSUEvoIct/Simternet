package simternet.launcher;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ec.Evolve;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * A graphical interface that lets the user customize and fine-tune some
 * parameters from the config files, before starting a run
 * 
 * @author graysonwright
 * 
 */
public class Launcher extends JPanel {

	protected String[]			args				= null;
	protected ParameterDatabase	params;

	private static final long	serialVersionUID	= 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame("ECJ Simternet Launcher");
		Launcher launcher = new Launcher(args);
		frame.setContentPane(launcher);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Creates a Launcher, using a file specified in arguments[]
	 * 
	 * @param arguments
	 *            the runtime arguments to pass to the simulation
	 */
	public Launcher(String[] arguments) {
		setArgs(arguments);

		params = Evolve.loadParameterDatabase(args);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		addIntParamPanel("breedthreads", "# of Threads for Breeding");
		addIntParamPanel("simternet.threads", "# of Threads for Simternet Evaluation");
		addIntParamPanel("generations", "# of Generations to evolve");
		addIntParamPanel("simternet.chunks", "# of Simternet Chunks per Generation");
		addIntParamPanel("pop.subpop.0.size", "# of NSPs per Generation");
		addIntParamPanel("pop.subpop.1.size", "# of ASPs per Generation");

		add(new BoolParameterPanel("checkpoint", false, "Turn on ECJ checkpointing"));
		add(new IntParameterPanel("checkpoint-modulo", 1, "# of generations between ECJ checkpoints"));

		add(new BoolParameterPanel("simternet.checkpoint", false, "Turn on Simternet serialization"));
		add(new IntParameterPanel("simterent.checkpoint-modulo", 1, "# of generations between simternet checkpoints"));

		JButton launchButton = new JButton("Launch");
		launchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Launcher.this.launchButtonPressed(arg0);
			}
		});
		this.add(launchButton);
	}

	/**
	 * Adds a BoolParameterPanel to the interface
	 * 
	 * @param string
	 *            the name of the parameter
	 * @param description
	 *            the user-friendly description to display
	 */
	@SuppressWarnings("unused")
	private void addBoolParamPanel(String string, String description) {
		Parameter p = new Parameter(string);
		this.add(new BoolParameterPanel(p.toString(), params.getBoolean(p, null, false), description));
	}

	/**
	 * Adds an integer parameter panel to the interface
	 * 
	 * @param s
	 *            the name of the parameter
	 * @param description
	 *            the user-friendly description to display
	 */
	protected void addIntParamPanel(String s, String description) {
		Parameter p = new Parameter(s);
		int value;
		try {
			value = params.getInt(p, null);
		} catch (NumberFormatException e) {
			value = 1;
		}
		this.add(new IntParameterPanel(p.toString(), value, description));
	}

	/**
	 * Adds a string paramter panel to the interface
	 * 
	 * @param string
	 *            the name of the parameter
	 * @param description
	 *            the user-friendly description to display
	 */
	@SuppressWarnings("unused")
	private void addStringParamPanel(String string, String description) {
		Parameter p = new Parameter(string);
		this.add(new StringParameterPanel(p.toString(), params.getString(p, null), description));
	}

	/**
	 * Called when the user clicks the "Launch" button.
	 * 
	 * launches the application, providing all of the original command-line
	 * arguments, as well as overriding arguments based on values the user
	 * changed
	 * 
	 * @param event
	 */
	protected void launchButtonPressed(ActionEvent event) {

		getTopLevelAncestor().setVisible(false);

		// the new list of arguments that we'll be running with
		ArrayList<String> overwriteArgs = new ArrayList<String>();

		// get all of the original arguments first...
		if (args != null) {
			for (String arg : args) {
				overwriteArgs.add(arg);
			}
		}

		// then add arguments for all of the parameters the user changed
		for (Component c : getComponents())
			if (c instanceof ParameterPanel) {
				ParameterPanel pPanel = (ParameterPanel) c;
				if (pPanel.hasChanged()) {
					String argument = pPanel.getParameter() + "=" + pPanel.getValue();
					overwriteArgs.add("-p");
					overwriteArgs.add(argument);
				}
			}

		String[] newArgs = new String[overwriteArgs.size()];
		for (int i = 0; i < overwriteArgs.size(); i++) {
			newArgs[i] = overwriteArgs.get(i);
		}

		/*
		 * // Print new arguments for debug
		 * System.out.println("New Arguments:"); for (String s : newArgs) {
		 * System.out.print(s + " "); } System.out.println();
		 */
		Evolve.main(newArgs);
	}

	public void setArgs(String[] args) {
		this.args = args;
		/*
		 * // Print original arguments for debug
		 * System.out.println("Original Arguments:"); for (String s : this.args)
		 * { System.out.print(s + " "); } System.out.println();
		 */
	}

}
