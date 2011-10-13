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

	public double				requestedUsage;
	public BackboneLink			link;

	public WFQCongestionAlgorithm(BackboneLink link) {
		this.link = link;
	}

	@Override
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
		requestedUsage = 0D;

		// will contain the total # of flow seconds (non-interactive flows will
		// extend time if congested)
		double requestedDuration = 0D;

		for (NetFlow flow : flows) {
			requestedUsage += flow.getActualTransfer();
			requestedDuration += flow.getCongestionDuration();
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
		double remainingDuration = requestedDuration;
		double maxBandwidth = 0D;

		// figure out what the maximum bandwidth is
		for (NetFlow flow : flows) {
			double flowBandwidth = flow.getCongestionBandwidth();
			if (flowBandwidth * remainingDuration < remainingCapacity) {
				// then the rest of the flows will still fit at this bandwidth
				// subtract our usage and see if the next flow will fit now
				remainingCapacity -= flow.getActualTransfer();
				requestedDuration -= flow.getCongestionDuration();
				continue;
			} else {
				// must split remaining capacity between remaining flows evenly
				maxBandwidth = remainingCapacity / remainingDuration;
				break;
			}
		}

		// congest all flows to that maximum bandwidth
		for (NetFlow flow : flows) {
			flow.congest(maxBandwidth);
		}

		return flows;
	}

	// @Override
	// public Double getUsageRatio() {
	// double usageRatio = requestedUsage / link.getBandwidth();
	// return usageRatio;
	// }

}
