package simternet.network;

import sim.engine.SimState;
import simternet.application.ApplicationServiceProvider;
import simternet.temporal.TemporalHashSet;

public class Datacenter extends AbstractNetwork {

	private static final long serialVersionUID = 1L;
	protected TemporalHashSet<NetFlow> inputQueue = new TemporalHashSet<NetFlow>();
	protected final ApplicationServiceProvider owner;

	public Datacenter(ApplicationServiceProvider owner) {
		this.owner = owner;
	}

	@Override
	public void createEgressLinkTo(AbstractNetwork an, BackboneLink l) {
		super.createEgressLinkTo(an, l);
	}

	@Override
	public void route() {
		for (NetFlow flow : this.inputQueue)
			this.route(flow);
	}

	public void send(NetFlow flow) {
		this.inputQueue.add(flow);
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		super.step(state);
	}

	@Override
	public String toString() {
		return "Datacenter of " + this.owner.getName();
	}

	@Override
	public void update() {
		super.update();
		this.inputQueue.update();
	}

}
