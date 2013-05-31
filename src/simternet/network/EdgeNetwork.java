package simternet.network;

import java.util.List;

import sim.engine.SimState;
import simternet.Simternet;
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

	/**
	 * congestion[aspID] = that ASP's congestion on this network.
	 */
	public double[] congestion;

	/**
	 * maxObservedBandwidth[aspID] = the maximum bandwidth seen from that asp to
	 * this edge network. Used for flow control.
	 */
	public double[] maxObservedBandwidth;

	/*************************
	 * Operational Variables *
	 *************************/
	int posX, posY;
	NSP owner;

	public EdgeNetwork(NSP owner, int posX, int posY) {
		this.owner = owner;
		this.posX = posX;
		this.posY = posY;

		this.congestion = new double[owner.s.allASPs.length];
		

		// TODO Edge networks currently have infinite bandwidth
		maxBandwidth = Double.MAX_VALUE;
		maxObservedBandwidth = new double[owner.s.allASPs.length];
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

	public void receivePayment(byte consumerID, double populationSize) {
		double totalRevenue = populationSize * this.price;
		owner.financials.earnRevenue(totalRevenue);
	}

	/**
	 * Retreive incoming flows from out Ingress links. Process them so that we
	 * have congestion and bandwidth stats.
	 */
	void netflowFinalProcess(Simternet s) {
		BackboneLink link = getUpstreamIngress();

		float[] requestedBW = new float[s.allASPs.length];
		float[] observedBW = new float[s.allASPs.length];

		// zero out max bw seen
		for (byte i = 0; i < maxObservedBandwidth.length; i++) {
			maxObservedBandwidth[i] = 0;
		}

		// grab/aggregate information from flows
		List<NetFlow> flows = link.receiveFlows();
		for (NetFlow flow : flows) {
			requestedBW[flow.aspID] += flow.bandwidthRequested;
			observedBW[flow.aspID] += flow.bandwidth;
			if (flow.bandwidth > maxObservedBandwidth[flow.aspID]) {
				maxObservedBandwidth[flow.aspID] = flow.bandwidth;
			}
			// information for data output
			owner.s.avgFlowBandwidthReceived.increment(flow.bandwidth);
			
		}

		// convert to congestion metric
		for (byte aspID = 0; aspID < requestedBW.length; aspID++) {
			double fracNewCongestion = s.congestionAdjustmentSpeed;
			double fracOldCongestion = 1 - s.congestionAdjustmentSpeed;
			
			double newCongestion = 0;
			if (requestedBW[aspID] > 0)
				newCongestion = 1 - (observedBW[aspID] / requestedBW[aspID]);

			congestion[aspID] = congestion[aspID] * fracOldCongestion
					+ fracNewCongestion * newCongestion;
		}
		
		
		
	}

	@Override
	public void step(SimState state) {
		super.step(state);
		netflowFinalProcess((Simternet) state);
	}

	@Override
	public String toString() {
		return owner + ".edge[" + posX + "][" + posY + "]";
	}
	
	

}
