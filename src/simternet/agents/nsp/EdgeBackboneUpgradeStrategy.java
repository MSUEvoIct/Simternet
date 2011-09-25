package simternet.agents.nsp;

import java.io.Serializable;

import simternet.engine.SimternetConfig;
import simternet.network.EdgeNetwork;

public class EdgeBackboneUpgradeStrategy implements Serializable {
	private static final long	serialVersionUID	= 1L;
	protected NetworkProvider	nsp;

	public EdgeBackboneUpgradeStrategy(NetworkProvider nsp) {
		this.nsp = nsp;
	}

	protected void upgradeNetwork(EdgeNetwork en, double capacityToAdd) {
		// Can't add a negative capacity
		if (capacityToAdd <= 0)
			return;

		// Can't add more than this ridiculous capacity
		if (capacityToAdd >= 1E25) {
			capacityToAdd = 1E25;
		}

		double cost = SimternetConfig.edgeBackboneUpgradeCost(en, capacityToAdd);
		double availableFinancing = nsp.financials.getAvailableFinancing();
		if (cost > availableFinancing)
			return; // the NSP can't afford this capacity
		// If it can, finance the upgrade.
		nsp.financials.capitalize(cost);
		// and execute the upgrade.
		double currentBandwidth = en.getUpstreamIngress().getBandwidth();
		double newBandwidth = currentBandwidth + capacityToAdd;
		en.getUpstreamIngress().setBandwidth(newBandwidth);
	}

	public void execute() {
		for (Object o : nsp.edgeNetworks) {
			EdgeNetwork en = (EdgeNetwork) o;
			double capacityToAdd = determineCapacityToAdd(en);
			upgradeNetwork(en, capacityToAdd);
		}
	}

	/**
	 * Determines how much bandwidth to add to each of this NSP's edges.
	 * 
	 * @param en
	 *            The edge network in question
	 * @return The amount of bandwidth to add
	 */
	public double determineCapacityToAdd(EdgeNetwork en) {
		// without a more specific strategy, don't upgrade at all
		return 0D;
	}

}
