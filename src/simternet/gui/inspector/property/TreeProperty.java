package simternet.gui.inspector.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import simternet.ecj.EvolvableAgent;
import ec.EvolutionState;
import ec.util.Log;
import ec.util.Output;

/**
 * Represents an EvolvableAgent's decision trees. Provides a button that allows
 * the user to print out a copy of the agent's tree
 * 
 * @author graysonwright
 * 
 */
public class TreeProperty extends Property implements ActionListener {

	protected EvolvableAgent		agent;
	protected JButton				printButton;
	protected static int			logNumber;
	private static final long		serialVersionUID	= 1L;
	protected static EvolutionState	state;

	/**
	 * Creates a TreeProperty with a given name representing a given agent
	 * 
	 * @param propertyName
	 *            the name to display for this property
	 * @param agent
	 *            the agent whose trees we'll be printing
	 */
	public TreeProperty(String propertyName, EvolvableAgent agent) {
		super(propertyName);

		this.agent = agent;

		printButton = new JButton("Print Tree");
		printButton.addActionListener(this);
		this.add(printButton);
	}

	/**
	 * Called when the user presses the printButton. Prints out a copy of the
	 * agent's decision trees.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Print agent's network tree

		if (TreeProperty.state == null) {
			TreeProperty.state = new EvolutionState();
			TreeProperty.state.output = new Output(false);
			TreeProperty.logNumber = TreeProperty.state.output.addLog(Log.D_STDOUT, false);
		}

		agent.getIndividual().printIndividualForHumans(TreeProperty.state, TreeProperty.logNumber);
	}

}
