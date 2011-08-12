package simternet.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import simternet.TraceConfig;
import simternet.application.ApplicationProvider;
import simternet.temporal.TemporalHashMap;
import simternet.temporal.TemporalHashSet;

public class Datacenter extends Network {

	private static final long					serialVersionUID	= 1L;
	protected TemporalHashSet<NetFlow>			inputQueue			= new TemporalHashSet<NetFlow>();
	/**
	 * Stores the congestion this application sees on each network. Congestion
	 * is stored as the amount of bandwidth actually received by the congested
	 * flow. I.e., you would need to compare this to the application's bandwidth
	 * use to calculate a percentage of congestion.
	 */
	protected TemporalHashMap<Network, Double>	observedBandwidth	= new TemporalHashMap<Network, Double>();
	protected final ApplicationProvider			owner;

	public Datacenter(ApplicationProvider owner) {
		this.owner = owner;
	}

	public Double getFractionExpected(EdgeNetwork en) {
		Double observedBandwidth = getObservedBandwidth(en);
		Double requestedBandwidth = owner.getBandwidth();
		if (observedBandwidth == null || requestedBandwidth == null)
			return 1.0;
		else
			return observedBandwidth / requestedBandwidth;
	}

	/**
	 * @param an
	 * @return The actual bandwidth received by congested flows at this edge
	 *         network.
	 */
	public Double getObservedBandwidth(Network an) {
		return observedBandwidth.get(an);
	}

	public ApplicationProvider getOwner() {
		return owner;
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
		if (TraceConfig.sanityChecks) {
			if (flow.destination == null)
				throw new RuntimeException("A packet going nowhere is congested?!");
		}
		observedBandwidth.put(flow.destination, flow.bandwidth);
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
		Double observedBandwidth = this.observedBandwidth.get(flow.destination);

		if (observedBandwidth != null) {
			double increasedBandwidth = observedBandwidth * (1 + owner.s.config.applicationFlowGrowthProportion);
			double minimumBandwidth = owner.getBandwidth() * owner.s.config.applicationFlowMinimumProportion;
			double bandwidth = 0D;

			if (increasedBandwidth < minimumBandwidth) {
				bandwidth = minimumBandwidth;
			} else {
				bandwidth = increasedBandwidth;
			}

			flow.congest(bandwidth);
		}

		inputQueue.add(flow);
	}

	public String printCongestion() {
		StringBuffer sb = new StringBuffer();

		sb.append("Congestion status of Egress Links\n");
		for (BackboneLink bb : egressLinks.values()) {
			sb.append(bb + " has usage factor of " + bb.congestionAlgorithm.getUsageRatio() + "\n");
		}

		sb.append("Congestion status of Edge Networks\n");

		ArrayList<Network> nets = new ArrayList(observedBandwidth.keySet());
		Collections.sort(nets, new Comparator<Network>() {

			/**
			 * Just used for sorting display by network.
			 */
			@Override
			public int compare(Network o1, Network o2) {
				return o1.toString().compareTo(o2.toString());
				// return 0;
			}
		});

		for (Network net : nets) {
			if (net == null)
				throw new RuntimeException("wtf?");
			sb.append(net.toString() + ": ObservedBW=" + observedBandwidth.get(net));
			sb.append(" (" + owner.getCongestionRatio(net) + ")");
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public void route() {
		for (NetFlow flow : inputQueue) {
			this.route(flow);
		}
		inputQueue = new TemporalHashSet<NetFlow>();
	}

	@Override
	public void step(SimState state) {
		super.step(state);

		if (TraceConfig.congestionASPSummary && Logger.getRootLogger().isTraceEnabled()) {
			Logger.getRootLogger().log(Level.TRACE, toString() + ": Congestion\n" + printCongestion());
		}
	}

	@Override
	public String toString() {
		return "Datacenter of " + owner.getName();
	}

	@Override
	public void update() {
		super.update();
		inputQueue.update();
		observedBandwidth.update();
	}

}
