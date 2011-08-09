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

	public Launcher(String[] arguments) {
		setArgs(arguments);

		params = Evolve.loadParameterDatabase(args);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		addIntParamPanel("breedthreads", "# of Threads for Breeding");
		addIntParamPanel("evalthreads", "# of Threads for Evaluation");
		addIntParamPanel("generations", "# of Generations");
		addBoolParamPanel("checkpoint", "Use ECJ Checkpointing");
		addStringParamPanel("prefix", "Prefix for ECH Checkpoint Files");
		addIntParamPanel("checkpoint-modulo", "ECJ Checkpoint modulo");
		addIntParamPanel("simternet.chunks", "# of Simternet Chunks per Generation");
		addBoolParamPanel("simternet.checkpoint", "Use Simternet Serialization Checkpointing");
		addIntParamPanel("simternet.checkpoint-modulo", "Simternet Checkpoint Modulo");
		addStringParamPanel("simternet.checkpoint.directory", "Directory for Simternet Checkpoint files");
		addIntParamPanel("pop.subpop.0.size", "Number of NSPs per Generation");
		addIntParamPanel("simternet.steps", "# of Steps to run each Simternet");
		addIntParamPanel("pop.subpop.1.size", "Number of ASPs per Generation");

		JButton launchButton = new JButton("Launch");
		launchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Launcher.this.launchButtonPressed(arg0);
			}
		});
		this.add(launchButton);
	}

	private void addBoolParamPanel(String string, String description) {
		Parameter p = new Parameter(string);
		this.add(new BoolParameterPanel(p.toString(), params.getBoolean(p, null, false), description));
	}

	protected void addIntParamPanel(String s, String description) {
		Parameter p = new Parameter(s);
		this.add(new IntParameterPanel(p.toString(), params.getInt(p, null), description));
	}

	private void addStringParamPanel(String string, String description) {
		Parameter p = new Parameter(string);
		this.add(new StringParameterPanel(p.toString(), params.getString(p, null), description));
	}

	protected void launchButtonPressed(ActionEvent event) {

		getTopLevelAncestor().setVisible(false);

		ArrayList<String> overwriteArgs = new ArrayList<String>();

		if (args != null) {
			for (String arg : args) {
				overwriteArgs.add(arg);
			}
		}

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
