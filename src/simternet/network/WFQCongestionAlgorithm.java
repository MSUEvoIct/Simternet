package simternet.network;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * WFQCongestionAlgorithm executes a maximin allocation of bandwidth. It
 * maximizes the minimum bandwidth a particular flow will receive.
 * 
 * @author kkoning
 * 
 */
public class WFQCongestionAlgorithm implements CongestionAlgorithm {

	protected BackboneLink link;

	public WFQCongestionAlgorithm(BackboneLink link) {
		this.link = link;
	}

	@Override
	public BackboneLink getLink() {
		return this.link;
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

		Double remainingCapacity = bottleneck.getBandwidth();
		Double remainingUsage = 0D;
		Double congestedDurationRemaining = 0D;

		for (NetFlow flow : flows) {
			remainingUsage += flow.getUsage();
			congestedDurationRemaining += flow.getCongestionDuration();
		}

		/*
		 * If the link has enough capacity, there is no need to ration ANY flow.
		 * Simply allow all flows to pass this link uncongested.
		 */
		if (remainingUsage < remainingCapacity) {
			Logger.getRootLogger().log(
					Level.DEBUG,
					this.getLink() + " uncongested, " + remainingUsage + "/"
							+ remainingCapacity);
			return flows;
		} else
			Logger.getRootLogger().log(
					Level.DEBUG,
					this.getLink() + " congested, " + remainingUsage + "/"
							+ remainingCapacity);

		/*
		 * Otherwise, allocate bandwidth to the slowest flows first.
		 */

		Double maxBandwidth = 0D;

		// should sort flows by slowest first, taking into account the amount to
		// which background flows will slow down before reducing usage
		Collections.sort(flows, new NetFlow.CongestionBandwidthComparator());

		// figure out what the maximum bandwidth is
		for (NetFlow flow : flows) {
			Double bandwidth = flow.getCongestionBandwidth();
			if ((bandwidth * congestedDurationRemaining) < remainingCapacity) {
				// then every flow can go at least this fast
				maxBandwidth = bandwidth;
				remainingCapacity -= flow.getUsage();
				congestedDurationRemaining -= flow.getCongestionDuration();
				continue;
			} else {
				// must split remaining capacity between remaining flows evenly
				maxBandwidth = remainingCapacity / congestedDurationRemaining;
				break;
			}
		}

		// congest all flows to that maximum bandwidth
		for (NetFlow flow : flows)
			flow.congest(maxBandwidth);

		return flows;
	}

}
