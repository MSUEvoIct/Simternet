package simternet.network;

import java.io.Serializable;
import java.text.DecimalFormat;
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

	protected Double			congestionRatio		= 0D;
	protected String			congestionReport;
	protected BackboneLink		link;

	private static final long	serialVersionUID	= 1L;

	public WFQCongestionAlgorithm(BackboneLink link) {
		this.link = link;
	}

	@Override
	public Double getCongestionRatio() {
		return this.congestionRatio;
	}

	public String getCongestionReport() {
		return this.congestionReport;
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

		// will contain the total bytes requested by all flows
		Double remainingUsage = 0D;
		// will contain the total # of flow seconds (non-interactive flows will
		// extend time if congested)
		Double congestedDurationRemaining = 0D;

		for (NetFlow flow : flows) {
			remainingUsage += flow.getUsage();
			congestedDurationRemaining += flow.getCongestionDuration();
		}

		StringBuffer cr = new StringBuffer();
		Double usageRatio = remainingUsage / bottleneck.getBandwidth();
		String percent = new DecimalFormat("000.#").format(usageRatio * 100);
		cr.append(percent + "%/" + bottleneck.getBandwidth());
		this.congestionReport = cr.toString();

		// Calculate the congestion ratio, store to be queried later.
		this.congestionRatio = remainingUsage / remainingCapacity;

		/*
		 * If the link has enough capacity, there is no need to ration ANY flow.
		 * Simply allow all flows to pass this link uncongested.
		 */
		if (remainingUsage < remainingCapacity)
			return flows;
		else {
		}

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
