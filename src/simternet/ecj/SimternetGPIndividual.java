package simternet.ecj;

import ec.gp.GPIndividual;

public class SimternetGPIndividual extends GPIndividual {

	private static final long	serialVersionUID	= 1L;
	private EvolvableAgent				agent;

	public EvolvableAgent getAgent() {
		return this.agent;
	}

	public void setAgent(EvolvableAgent agent) {
		this.agent = agent;
	}

}
