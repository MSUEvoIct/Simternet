package simternet.network;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * WFQCongestionAlgorithm executes a maximin allocation of bandwidth. It
 * maximizes the minimum bandwidth a particular flow will receive.
 * 
 * @author kkoning
 * 
 */
public class WFQCongestionAlgorithm implements CongestionAlgorithm, Serializable {
	private static final long	serialVersionUID	= 1L;

	public BackboneLink			link;

	public WFQCongestionAlgorithm(BackboneLink link) {
		this.link = link;
	}

	public BackboneLink getLink() {
		return link;
	}

	/*
	 * TODO: Distribute remaining BW from last jump? Might make a difference if
	 * things fit just as there's a huge jump down in BW.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.network.CongestionAlgorithm#limit(java.util.List,
	 * simternet.network.BackboneLink)
	 */
	@Override
	public List<NetFlow> limit(List<NetFlow> flows, BackboneLink bottleneck) {
		double linkCapacity = bottleneck.getBandwidth();

		// will contain the total bytes requested by all flows
		double requestedUsage = 0D;
		int numFlows = flows.size();

		for (NetFlow flow : flows) {
			requestedUsage += flow.bandwidth;
		}

		/*
		 * If the link has enough capacity, there is no need to ration ANY flow.
		 * Simply allow all flows to pass this link uncongested.
		 */
		if (requestedUsage <= linkCapacity)
			return flows;

		
		/*
		 * We'll start by allocating bandwidth to the slowest flows first. If
		 * these flows are slower than our average, that surplus can be used by
		 * other flows.
		 */
		Collections.sort(flows, new NetFlow.CongestionBandwidthComparator());

		double remainingCapacity = linkCapacity;
		int remainingFlows = numFlows +1 ; // 1 free flow to prevent / 0 error
		double maxBW = 0;
		boolean bwMaxed = false;

		for (NetFlow flow : flows) {
			if (bwMaxed) {
				flow.congest((float)maxBW);
			} else {
				double avgBW = remainingCapacity / remainingFlows;
				if (flow.bandwidth < avgBW) {
					// flow does not need to be congested
					// have that much less capacity for next flow
					remainingCapacity -= flow.bandwidth;
				} else {
					flow.congest((float)avgBW);
					maxBW = avgBW;
					bwMaxed = true;
				}
			}
			
		}

		return flows;
	}

	// @Override
	// public Double getUsageRatio() {
	// double usageRatio = requestedUsage / link.getBandwidth();
	// return usageRatio;
	// }

}
