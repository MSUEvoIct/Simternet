package simternet.network;

import java.util.List;

import simternet.nsp.NSP;

public class EdgeNetwork extends Network {
	private static final long serialVersionUID = 1L;

	/*********************************
	 * Economicly Relevent Variables *
	 *********************************/
	public double price;
	public double totalRevenue;
	public double totalOpCost;
	/**
	 * The maximum bandwidth each edge connection can support. Unlike other
	 * networks, this is an instantaneous measure rather than a total transfer
	 * capacity per period. I.e., bytes per second, not bytes per month. TODO:
	 * Is this still correct?
	 */
	double maxBandwidth;

	/*************************
	 * Operational Variables *
	 *************************/
	byte posX, posY;
	NSP owner;

	public EdgeNetwork(NSP owner, byte posX, byte posY) {
		this.owner = owner;
		this.posX = posX;
		this.posY = posY;

		// TODO Edge networks currently have infinite bandwidth
		maxBandwidth = Double.MAX_VALUE;
	}

	BackboneLink getUpstreamIngress() {
		int i = 0;
		BackboneLink l = null;
		for (BackboneLink link : ingressLinks.values()) {
			i++;
			l = link;
		}
		if (i == 1)
			return l;

		return null;
	}

	/**
	 * Retreive incoming flows from out Ingress links, process them, and send
	 * them to consumers.
	 */
	public void sendFlowsToCustomers() {
		/*
		 * We need to iterate over every flow, received on every ingress link.
		 * (Actually, there should be only one, since this is an edge network,
		 * but other networks can have many ingress links.)
		 */
		for (BackboneLink link : ingressLinks.values()) {
			List<NetFlow> flows = link.receiveFlows();
			for (NetFlow flow : flows) {
				/*
				 * Process congestion information first. This includes 1)
				 * further congesting to the maximum bandwidth of this edge
				 * network, 2) informing the sending network of the congestion,
				 * 3) noting the congestion ourselves.
				 */
				// kk-bug? flow.congest(getMaxBandwidth());
				if (flow.isCongested()) {
					flow.source.noteCongestion(flow);
					noteCongestion(flow);
				}

				/*
				 * Flows are now ready to be received by the users. What users
				 * do with them from here is up to them. They may discard the
				 * information, track statistics, take certain actions, etc...
				 */
				flow.user.receiveFlow(flow);

			}
		}
	}

	public void receivePayment(byte consumerID, double populationSize) {
		double totalRevenue = populationSize * this.price;
		owner.financials.earnRevenue(totalRevenue);
		
	}

}
