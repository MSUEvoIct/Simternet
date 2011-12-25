package simternet.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sim.engine.SimState;
import simternet.agents.asp.ApplicationProvider;
import simternet.engine.TraceConfig;
import simternet.engine.asyncdata.TemporalArrayList;
import simternet.engine.asyncdata.TemporalHashMap;

/**
 * DataCenters are the networks run by ApplicationProviders. In addition to the
 * functions of a standard network (i.e., routing, etc...), DataCenters provide
 * functionality required by ApplicationProviders, such as originating traffic
 * into the network and tracking information on congestion characteristics for
 * the use of Consumers and the ApplicationProvider.
 * 
 * @author kkoning
 * 
 */
public class DataCenter extends Network {

	private static final long					serialVersionUID	= 1L;
	protected TemporalArrayList<NetFlow>		inputQueue			= new TemporalArrayList<NetFlow>();
	/**
	 * Stores the congestion this application sees on each network. Congestion
	 * is stored as the amount of bandwidth actually received by the congested
	 * flow. I.e., you would need to compare this to the application's bandwidth
	 * use to calculate a percentage of congestion.
	 */
	protected TemporalHashMap<Network, Double>	observedBandwidth	= new TemporalHashMap<Network, Double>();
	protected final ApplicationProvider			owner;

	public DataCenter(ApplicationProvider owner) {
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
		if (TraceConfig.networking.aspSentFlow) {
			TraceConfig.out.println(this + " originating " + flow + " to " + flow.user);
		}

		Double observedBandwidth = this.observedBandwidth.get(flow.destination);
		if (TraceConfig.networking.aspFlowControl) {
			TraceConfig.out.println(this + " observed max BW at destination to be " + observedBandwidth);
		}

		if (observedBandwidth != null && observedBandwidth * 1.0001 < flow.bandwidthRequested) {
			double increasedBandwidth = observedBandwidth * (1 + owner.s.config.applicationFlowGrowthProportion);
			double minimumBandwidth = owner.getBandwidth() * owner.s.config.applicationFlowMinimumProportion;
			double bandwidth = Double.NaN;

			if (increasedBandwidth < minimumBandwidth) {
				if (TraceConfig.networking.aspFlowControl) {
					TraceConfig.out.println(this + " minimum banwidth " + minimumBandwidth
							+ " exceeds flow-control suggested rate of " + increasedBandwidth
							+ ", trying minimum instead");
				}
				bandwidth = minimumBandwidth;
			} else {
				bandwidth = increasedBandwidth;
			}

			if (TraceConfig.networking.aspFlowControl) {
				TraceConfig.out.println(this + " flow control suggests trying bw=" + bandwidth);
			}

			if (bandwidth < flow.bandwidthRequested) {
				flow.congest(bandwidth);
			}
		}

		// Immediately route the flow to the proper output backbone link.
		// Look in our input queue
		this.route(flow);
	}

	public String printCongestion() {
		StringBuffer sb = new StringBuffer();

		sb.append("Congestion status of Egress Links\n");
		for (BackboneLink bb : egressLinks.values()) {
			sb.append(bb + " has usage factor of " + bb.perStepCongestionRatio() + "\n");
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
			sb.append(" (" + owner.getExpectedFraction(net) + ")");
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public void step(SimState state) {
		super.step(state);

		if (TraceConfig.networking.congestionASPSummary) {
			TraceConfig.out.println(this + ": Congestion\n" + printCongestion());
		}
	}

	@Override
	public String toString() {
		return "DC of " + owner.getName();
	}

	@Override
	public void update() {
		super.update();
		observedBandwidth.update();
	}

}
