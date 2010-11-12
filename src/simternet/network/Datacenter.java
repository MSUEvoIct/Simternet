package simternet.network;

import sim.engine.SimState;
import simternet.application.ApplicationServiceProvider;
import simternet.temporal.TemporalHashMap;
import simternet.temporal.TemporalHashSet;

public class Datacenter extends AbstractNetwork {

	private static final long serialVersionUID = 1L;
	protected TemporalHashMap<AbstractNetwork, Double> congestion = new TemporalHashMap<AbstractNetwork, Double>();
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
	public void noteCongestion(NetFlow flow) {
		this.congestion.put(flow.destination, flow.bandwidth);
	}

	/**
	 * Originate traffic, inject it into the network. This method should be
	 * called by methods related to customer usage, not methods related to the
	 * operation of the network.
	 * 
	 * @param flow
	 */
	public void originate(NetFlow flow) {
		// Check to see if this flow was congested in previous periods. If so,
		// pre-congest it to just faster than last period.
		Double congestedBandwidth = this.congestion.get(flow.destination);
		if (congestedBandwidth != null)
			flow.congest(congestedBandwidth * 1.1);

		this.inputQueue.add(flow);
	}

	@Override
	public void route() {
		for (NetFlow flow : this.inputQueue)
			this.route(flow);
		this.inputQueue = new TemporalHashSet<NetFlow>();
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
		this.congestion.update();
	}

}
