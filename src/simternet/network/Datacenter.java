package simternet.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import simternet.application.ApplicationServiceProvider;
import simternet.temporal.TemporalHashMap;
import simternet.temporal.TemporalHashSet;

public class Datacenter extends AbstractNetwork {

	private static final long serialVersionUID = 1L;
	/**
	 * Stores the congestion this application sees on each network. Congestion
	 * is stored as the amount of bandwidth actually received by the congested
	 * flow. I.e., you would need to compare this to the application's bandwidth
	 * use to calculate a percentage of congestion.
	 */
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

	/**
	 * @param an
	 * @return The actual bandwidth received by congested flows at this edge
	 *         network.
	 */
	public Double getCongestion(AbstractNetwork an) {
		return this.congestion.get(an);
	}

	/*
	 * Once a flow is finally sent to a customer, this method should be called
	 * so that the application provider can be aware of how its application
	 * either is or is not congested.
	 * 
	 * @see
	 * simternet.network.AbstractNetwork#noteCongestion(simternet.network.NetFlow
	 * )
	 */
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

	public String printCongestion() {
		StringBuffer sb = new StringBuffer();

		ArrayList<AbstractNetwork> nets = new ArrayList(this.congestion
				.keySet());
		Collections.sort(nets, new Comparator<AbstractNetwork>() {

			/**
			 * Just used for sorting display by network.
			 */
			@Override
			public int compare(AbstractNetwork o1, AbstractNetwork o2) {
				return o1.toString().compareTo(o2.toString());
				// return 0;
			}
		});

		for (AbstractNetwork net : nets)
			sb.append(net.toString() + ": " + this.congestion.get(net) + "\n");

		return sb.toString();
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
		Logger.getRootLogger().log(Level.DEBUG,
				this.toString() + ": Congestion\n" + this.printCongestion());
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
