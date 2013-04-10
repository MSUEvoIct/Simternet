package simternet.network;

import sim.engine.SimState;
import simternet.TraceConfig;
import simternet.asp.ASP;

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
	private static final long serialVersionUID = 1L;

	/**
	 * The ApplicationProvider which operates this DataCenter.
	 */
	protected final ASP owner;


	public DataCenter(ASP owner) {
		this.owner = owner;
	}

//	/**
//	 * Function used in flow control and consumption functions to retrieve the
//	 * expected congestion of this ApplicationProvider on the specified
//	 * EdgeNetwork. If there is no information, the expectation is that there
//	 * will be zero congestion.
//	 * 
//	 * TODO: Limit this to the maximum bandwidhth of the EdgeNetwork's
//	 * "last-mile" connections
//	 * 
//	 * @param en
//	 *            The target EdgeNetwork
//	 * @return The ratio of observed to requested bandwidth [0->1]
//	 */
//	public Double getFractionExpected(EdgeNetwork en) {
//		Double obsBW = this.observedBandwidth.get(en);
//		Double requestedBandwidth = owner.getBandwidth();
//		double fractionExpected = 1.0;
//
//		if (obsBW != null && requestedBandwidth != null)
//			fractionExpected = obsBW / requestedBandwidth;
//
//		// Sanity Check
//		if (TraceConfig.sanityChecks)
//			if (fractionExpected < 0 || fractionExpected > 1)
//				throw new RuntimeException(
//						"Expected fraction of requested bandwidth cannot be outside the range [0->1]");
//
//		return fractionExpected;
//	}

	/**
	 * Originate traffic; inject it into the network. This method should only be
	 * invoked by the ApplicationProvider who owns this network.
	 * 
	 * @param flow
	 *            A NetFlow object representing the network resources requested
	 *            by the use of this application.
	 */
	public void originate(NetFlow flow) {
		// Check to see if this flow was congested in previous periods. If so,
		// pre-congest it to just faster than last period.
		if (TraceConfig.networking.aspSentFlow) {
			TraceConfig.out.println(this + " originating " + flow + " to "
					+ flow.destination);
		}

		EdgeNetwork destEdge = (EdgeNetwork) flow.destination;
		
		float estimatedBandwidth = destEdge.maxObservedBandwidth[owner.id];
				
		if (TraceConfig.networking.aspFlowControl) {
			TraceConfig.out.println(this
					+ " observed max BW at destination to be "
					+ estimatedBandwidth);
		}

		// Try to increase the bandwidth by a fixed proportion (i.e., 10%)
		float growthRatio = (1 + owner.s.applicationFlowGrowthProportion);
		if (estimatedBandwidth < flow.bandwidthRequested)
			estimatedBandwidth = estimatedBandwidth * growthRatio;

		// But make sure it's not too low.  Lower threshold same proportion
		// of the requested bandwidth
		float minimumBandwidth = flow.bandwidthRequested
				* owner.s.applicationFlowGrowthProportion;
		if (estimatedBandwidth < minimumBandwidth)
			estimatedBandwidth = minimumBandwidth;

		// But never more than 100% of the requested bandwidth
		if (estimatedBandwidth > flow.bandwidthRequested)
			estimatedBandwidth = flow.bandwidthRequested;

		if (estimatedBandwidth < flow.bandwidthRequested) {
			if (TraceConfig.networking.aspFlowControl) {
				TraceConfig.out.println(this + " flow control observed bw="
						+ ", trying bw=" + estimatedBandwidth);
			}
			flow.congest(estimatedBandwidth);
		}

		// Immediately route the flow to the proper output backbone link.
		this.route(flow);
	}

//	/**
//	 * Debug routine used for reporting DataCenter-wide congestion metrics.
//	 * 
//	 * @return a formatted String describing congestion per EdgeNetwork.
//	 */
//	public String printCongestion() {
//		StringBuffer sb = new StringBuffer();
//
//		sb.append("Congestion status of Egress Links\n");
//		for (BackboneLink bb : egressLinks.values()) {
//			sb.append(bb + " has usage factor of "
//					+ bb.perStepCongestionRatio() + "\n");
//		}
//
//		sb.append("Congestion status of Edge Networks\n");
//
//		ArrayList<Network> nets = new ArrayList(observedBandwidth.keySet());
//		Collections.sort(nets, new Comparator<Network>() {
//
//			/**
//			 * Just used for sorting display by network.
//			 */
//			@Override
//			public int compare(Network o1, Network o2) {
//				return o1.toString().compareTo(o2.toString());
//				// return 0;
//			}
//		});
//
//		for (Network net : nets) {
//			if (net == null)
//				throw new RuntimeException("wtf?");
//			sb.append(net.toString() + ": ObservedBW="
//					+ observedBandwidth.get(net));
//			sb.append(" (" + owner.getFractionExpected((EdgeNetwork) net) + ")");
//			sb.append("\n");
//		}
//
//		return sb.toString();
//	}

	@Override
	public void step(SimState state) {
		super.step(state);

//		if (TraceConfig.networking.congestionASPSummary) {
//			TraceConfig.out
//					.println(this + ": Congestion\n" + printCongestion());
//		}
	}

	@Override
	public String toString() {
		return "DataCenter of " + owner;
	}

}
