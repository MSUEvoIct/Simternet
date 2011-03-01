package simternet.nsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sim.util.Int2D;
import simternet.network.EdgeNetwork;
import simternet.network.SimpleEdgeNetwork;

/**
 * @author kkoning
 * 
 *         Builds each type of network at each location on the grid,
 *         sequentially, starting at 0.0.
 * 
 */
public class BuildEverywhereStrategy implements InvestmentStrategy,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Boolean built = false;
	protected Integer builtThroughX = 0;
	protected Integer builtThroughY = 0;
	protected List<Class<? extends EdgeNetwork>> networkTypes;
	protected NetworkProvider nsp;

	/**
	 * Build SimpleNetwork everywhere...
	 * 
	 * @param nsp
	 */
	public BuildEverywhereStrategy(NetworkProvider nsp) {
		this(nsp, null);
	}

	/**
	 * @param nsp
	 *            The NSP implmenting this strategy. We need to know because,
	 *            e.g., the costs for building a network may vary here.
	 * @param networks
	 *            A list of networks to be built. If non
	 * @param networkTypes
	 *            if null, just build SimpleNetwork. Otherwise, build all
	 *            network types in this list at each grid location.
	 */
	public BuildEverywhereStrategy(NetworkProvider nsp,
			List<Class<? extends EdgeNetwork>> networkTypes) {
		this.nsp = nsp;
		if (networkTypes == null) {
			networkTypes = new ArrayList<Class<? extends EdgeNetwork>>();
			networkTypes.add(SimpleEdgeNetwork.class);
		}
		this.networkTypes = networkTypes;
	}

	@Override
	public void makeNetworkInvestment() {

		if (this.built == true)
			return;

		Double amountAvailable = this.nsp.financials.getAvailableFinancing();

		// Figure out costs, build if we can afford to. All or nothing for each
		// square.
		for (int x = this.builtThroughX; x < this.nsp.simternet.config.x(); x++) {
			for (int y = this.builtThroughY; y < this.nsp.simternet.config
					.y(); y++) {
				Double costForThisPixel = 0.0;

				// figure out the cost to build one of each network at this
				// location. In order to do this without actually creating a
				// network
				// (i.e., instantiating the class), use reflection to call a
				// static
				// method instead.
				for (Class<? extends EdgeNetwork> cl : this.networkTypes)
					costForThisPixel = +EdgeNetwork.getBuildCost(cl,
							this.nsp, new Int2D(x, y));

				// if we have enough funding available to build the networks,
				// do so. When we run out of money, stop building.
				if (costForThisPixel < amountAvailable) {

					for (Class<? extends EdgeNetwork> cl : this.networkTypes)
						try {
							this.nsp.buildNetwork(cl, new Int2D(x, y));
							amountAvailable -= costForThisPixel;
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					this.builtThroughY = y + 1;
				} else
					return;

			}
			this.builtThroughX = x + 1;
			this.builtThroughY = 0;
		}

		this.built = true;

	}

}
