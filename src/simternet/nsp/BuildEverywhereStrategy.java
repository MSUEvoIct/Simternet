package simternet.nsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sim.field.grid.SparseGrid2D;
import simternet.network.AbstractNetwork;
import simternet.network.SimpleNetwork;

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
	protected SparseGrid2D networks;
	protected List<Class<? extends AbstractNetwork>> networkTypes;
	protected AbstractNetworkProvider nsp;

	public BuildEverywhereStrategy(AbstractNetworkProvider nsp,
			SparseGrid2D networks) {
		this(nsp, networks, null);
	}

	/**
	 * @param nsp
	 * @param networks
	 * @param networkTypes
	 *            if null, just build SimpleNetwork. Otherwise, build all
	 *            network types in this list at each grid location.
	 */
	public BuildEverywhereStrategy(AbstractNetworkProvider nsp,
			SparseGrid2D networks,
			List<Class<? extends AbstractNetwork>> networkTypes) {
		this.nsp = nsp;
		this.networks = networks;
		if (networkTypes == null) {
			networkTypes = new ArrayList<Class<? extends AbstractNetwork>>();
			networkTypes.add(SimpleNetwork.class);
		}
		this.networkTypes = networkTypes;
	}

	@Override
	public void makeNetworkInvestment() {

		if (this.built == true)
			return;

		Double amountAvailable = this.nsp.investor.getAvailableFinancing();

		// Figure out costs, build if we can afford to. All or nothing for each
		// square.
		for (int x = this.builtThroughX; x < this.nsp.simternet.parameters.x(); x++) {
			for (int y = this.builtThroughY; y < this.nsp.simternet.parameters
					.y(); y++) {
				Double costForThisPixel = 0.0;
				List<AbstractNetwork> nets = new ArrayList<AbstractNetwork>();

				// figure out the cost to build one of each network at this
				// location.
				for (Class<? extends AbstractNetwork> cl : this.networkTypes)
					try {
						AbstractNetwork an = cl.newInstance();
						an.init(this.nsp, x, y);
						costForThisPixel = an.getBuildCost();
						nets.add(an);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

				// if we have enough funding available to build the networks, do
				// so. When we run out of money, stop building.
				if (costForThisPixel < amountAvailable) {
					for (AbstractNetwork an : nets) {
						this.nsp.buildNetwork(an);
						amountAvailable -= costForThisPixel;
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
