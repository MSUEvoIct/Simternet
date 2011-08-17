package simternet.gui.inspector.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import simternet.ecj.EvolvableAgent;
import ec.EvolutionState;
import ec.util.Log;
import ec.util.Output;

public class TreeProperty extends Property implements ActionListener {

	protected EvolvableAgent		agent;
	protected JButton				printButton;
	protected static int			logNumber;
	private static final long		serialVersionUID	= 1L;
	protected static EvolutionState	state;

	public TreeProperty(String propertyName, EvolvableAgent agent) {
		super(propertyName);

		this.agent = agent;

		this.printButton = new JButton("Print Tree");
		this.printButton.addActionListener(this);
		this.add(this.printButton);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Print agent's network tree

		if (TreeProperty.state == null) {
			TreeProperty.state = new EvolutionState();
			TreeProperty.state.output = new Output(false);
			TreeProperty.logNumber = TreeProperty.state.output.addLog(Log.D_STDOUT, false);
		}

		this.agent.getIndividual().printIndividualForHumans(TreeProperty.state, TreeProperty.logNumber);
	}

}
