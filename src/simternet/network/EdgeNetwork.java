package simternet.network;

import java.util.List;

import sim.engine.SimState;
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
	float[] congestion;

	/*************************
	 * Operational Variables *
	 *************************/
	byte posX, posY;
	NSP owner;

	public EdgeNetwork(NSP owner, byte posX, byte posY) {
		this.owner = owner;
		this.posX = posX;
		this.posY = posY;

		this.congestion = new float[owner.s.allASPs.length];
		
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

	public void receivePayment(byte consumerID, double populationSize) {
		double totalRevenue = populationSize * this.price;
		owner.financials.earnRevenue(totalRevenue);
	}

	/**
	 * Retreive incoming flows from out Ingress links. Process them so that we
	 * have congestion and bandwidth stats.
	 */
	void netflowFinalProcess() {
		BackboneLink link = getUpstreamIngress();

		List<NetFlow> flows = link.receiveFlows();
		for (NetFlow flow : flows) {
			/*
			 * If a flow is congested, we want to
			 * TODO: Finish 
			 */
		}

	}

	@Override
	public void step(SimState state) {
		super.step(state);
		netflowFinalProcess();
	}

}
