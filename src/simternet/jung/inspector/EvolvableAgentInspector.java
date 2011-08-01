package simternet.jung.inspector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import simternet.ecj.EvolvableAgent;
import simternet.jung.gui.GUI;
import ec.EvolutionState;
import ec.util.Log;
import ec.util.Output;

public abstract class EvolvableAgentInspector extends Inspector implements ActionListener {

	protected EvolutionState	state;
	protected static int		logNumber;
	private static final long	serialVersionUID	= 1L;

	public EvolvableAgentInspector(Object object, GUI owner) {
		super(object, owner);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Print agent's network tree

		if (this.state == null) {
			this.state = new EvolutionState();
			this.state.output = new Output(false);
			EvolvableAgentInspector.logNumber = this.state.output.addLog(Log.D_STDOUT, false);
		}

		((EvolvableAgent) this.object).getIndividual().printIndividualForHumans(this.state,
				EvolvableAgentInspector.logNumber);
	}

}
