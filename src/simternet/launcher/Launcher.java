package simternet.launcher;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ec.Evolve;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class Launcher extends JPanel {

	protected String[]							args				= null;
	protected ParameterDatabase					params;
	protected static HashMap<String, String>	descriptionMapping;

	private static final long					serialVersionUID	= 1L;

	static {
		Launcher.descriptionMapping = new HashMap<String, String>();
		Launcher.descriptionMapping.put("breedthreads", "# of Threads for Breeding");
		Launcher.descriptionMapping.put("evalthreads", "# of Threads for Evaluation");
		Launcher.descriptionMapping.put("checkpoint", "Use ECJ Checkpointing");
		Launcher.descriptionMapping.put("generations", "# of Generations");
		Launcher.descriptionMapping.put("prefix", "Prefix for ECH Checkpoint Files");
		Launcher.descriptionMapping.put("checkpoint-modulo", "ECJ Checkpoint modulo");
		Launcher.descriptionMapping.put("simternet.chunks", "# of Simternet Chunks per Generation");
		Launcher.descriptionMapping.put("simternet.checkpoint", "Use Simternet Serialization Checkpointing");
		Launcher.descriptionMapping.put("simternet.checkpoint-modulo", "Simternet Checkpoint Modulo");
		Launcher.descriptionMapping.put("simternet.checkpoint.directory", "Directory for Simternet Checkpoint files");
		Launcher.descriptionMapping.put("pop.subpop.0.size", "Number of NSPs per Generation");
		Launcher.descriptionMapping.put("simternet.steps", "# of Steps to run each Simternet");
		Launcher.descriptionMapping.put("pop.subpop.1.size", "Number of ASPs per Generation");
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("ECJ Simternet Launcher");
		Launcher launcher = new Launcher(args);
		frame.setContentPane(launcher);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public Launcher(String[] arguments) {
		this.setArgs(arguments);

		this.params = Evolve.loadParameterDatabase(this.args);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.addIntParamPanel("breedthreads");
		this.addIntParamPanel("evalthreads");
		this.addIntParamPanel("generations");
		this.addBoolParamPanel("checkpoint");
		this.addStringParamPanel("prefix");
		this.addIntParamPanel("checkpoint-modulo");
		this.addIntParamPanel("simternet.chunks");
		this.addBoolParamPanel("simternet.checkpoint");
		this.addIntParamPanel("simternet.checkpoint-modulo");
		this.addStringParamPanel("simternet.checkpoint.directory");
		this.addIntParamPanel("pop.subpop.0.size");
		this.addIntParamPanel("simternet.steps");
		this.addIntParamPanel("pop.subpop.1.size");

		JButton launchButton = new JButton("Launch");
		launchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Launcher.this.launchButtonPressed(arg0);
			}
		});
		this.add(launchButton);
	}

	private void addBoolParamPanel(String string) {
		Parameter p = new Parameter(string);
		this.add(new BoolParameterPanel(p.toString(), this.params.getBoolean(p, null, false),
				Launcher.descriptionMapping.get(p.toString())));
	}

	protected void addIntParamPanel(String s) {
		Parameter p = new Parameter(s);
		this.add(new IntParameterPanel(p.toString(), this.params.getInt(p, null), Launcher.descriptionMapping.get(p
				.toString())));
	}

	private void addStringParamPanel(String string) {
		Parameter p = new Parameter(string);
		this.add(new StringParameterPanel(p.toString(), this.params.getString(p, null), Launcher.descriptionMapping
				.get(p.toString())));
	}

	protected void launchButtonPressed(ActionEvent event) {

		this.getTopLevelAncestor().setVisible(false);

		ArrayList<String> overwriteArgs = new ArrayList<String>();

		if (this.args != null)
			for (String arg : this.args)
				overwriteArgs.add(arg);

		for (Component c : this.getComponents())
			if (c instanceof ParameterPanel) {
				ParameterPanel pPanel = (ParameterPanel) c;
				if (pPanel.hasChanged()) {
					String argument = pPanel.getParameter() + "=" + pPanel.getValue();
					overwriteArgs.add("-p");
					overwriteArgs.add(argument);
				}
			}

		String[] newArgs = new String[overwriteArgs.size()];
		for (int i = 0; i < overwriteArgs.size(); i++)
			newArgs[i] = overwriteArgs.get(i);

		System.out.println("New Arguments:");
		for (String s : newArgs)
			System.out.print(s + " ");
		System.out.println();

		Evolve.main(newArgs);
	}

	public void setArgs(String[] args) {
		this.args = args;
		System.out.println("Original Arguments:");
		for (String s : this.args)
			System.out.print(s + " ");
		System.out.println();
	}

}
