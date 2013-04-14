package simternet.nsp;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.Financials;
import simternet.Simternet;
import simternet.TraceConfig;
import simternet.consumer.Consumer;
import simternet.network.Backbone;
import simternet.network.EdgeNetwork;
import simternet.network.RoutingProtocolConfig;

public class NSP implements Steppable {
	private static final long serialVersionUID = 1L;

	// Limit insane prices to prevent/detect bugs
	private static final double maxAspTransitPrice = 1E9;
	private static final double minAspTransitPrice = -1E9;

	public byte id;

	// Financial condition of this NSP
	public Financials financials = new Financials();

	// Bandwidth transit prices for other firms
	boolean aspPriceDiscriminationAllowed;
	double aspTransitPrice; // if per-ASP prices disallowed
	double[] aspTransitPrices; // if per-ASP prices allowed

	boolean nspPriceDiscriminationAllowed;
	double nspTransitPrice; // if per-NSP prices disallowed
	double[] nspTransitPrices; // if per-NSP prices allowed

	// Network Operations
	public Backbone backbone;
	public EdgeNetwork[][] edgeNetworks;

	// Reference to the main sumulation object
	public Simternet s;

	// The individual/agent controlling this NSP.
	public NSPIndividual ind;

	public NSP(Simternet simternet, NSPIndividual ind, byte nspID) {
		this.s = simternet;
		this.ind = ind;
		this.id = nspID;
		backbone = new Backbone(this);
		edgeNetworks = new EdgeNetwork[s.landscapeSizeX][s.landscapeSizeY];
	}

	/**
	 * Builds an edge network of type at location, capitalizes the construction
	 * costs, and connects the network to its backbone.
	 * 
	 * @param type
	 *            The class of the network to be built
	 * @param location
	 *            The location of the network.
	 */
	@SuppressWarnings("unused")
	public void buildNetwork(byte x, byte y) {
		// DEBUG
		if (false)
			System.out.println(this + " building edge @ " + x + "," + y);

		// Make sure there isn't already an edge network at this location
		if (edgeNetworks[x][y] != null)
			throw new RuntimeException("Edge network already built at " + x
					+ "," + y);

		float cost = 0;
		cost += s.edgeBuildCostFixed;
		cost += s.edgeBuildCostPerUser * s.getTotalPopulation(x, y);
		financials.invest(cost);

		edgeNetworks[x][y] = new EdgeNetwork(this, x, y);

		// for now, create a fixed bandwidth link to this network.
		backbone.createEgressLinkTo(edgeNetworks[x][y], s.edgeInitialBandwidth,
				RoutingProtocolConfig.NONE);

	}

	public double getASPTransitPrice(int aspID) {
		if (aspTransitPrices == null)
			return aspTransitPrices[aspID];
		else
			return aspTransitPrice;
	}

	private double limitAspTransitPrice(double proposedPrice) {
		if (Double.isNaN(proposedPrice)) {
			throw new RuntimeException("NaN ASP transit price");
		} else if (proposedPrice < minAspTransitPrice) {
			return minAspTransitPrice;
		} else if (proposedPrice > maxAspTransitPrice) {
			return maxAspTransitPrice;
		} else {
			return proposedPrice;
		}
	}

	public void setASPTransitPrice(byte aspID, double price) {
		aspTransitPrices[aspID] = limitAspTransitPrice(price);
	}

	/**
	 * @return the total number of customers the provider currently has.
	 */
	@SuppressWarnings("unchecked")
	public double getCustomers() {
		double numCustomers = 0.0;
		for (Consumer c : s.allConsumers) {
			numCustomers += c.getNSPSubscrubers(id);
		}
		return numCustomers;
	}

	/**
	 * @return The total number of customers at the specified location.
	 */
	@SuppressWarnings("unchecked")
	public double getCustomers(byte x, byte y) {
		Double numCustomers = 0.0;
		for (Consumer c : s.allConsumers) {
			numCustomers += c.getNSPSubscribers(x, y, id);
		}
		return numCustomers;
	}

	/**
	 * Dispose of all assets, exit market
	 */
	private void goBankrupt() {
		// TODO: Complete
	}

	public boolean hasNetworkAt(byte x, byte y) {
		if (edgeNetworks[x][y] == null)
			return false;
		else
			return true;
	}

	// private StringBuffer printAllNetworkGrid() {
	// StringBuffer sb = new StringBuffer();
	// DecimalFormat positionFormat = new DecimalFormat("00");
	// DecimalFormat numCustFormat = new DecimalFormat("0000000");
	//
	// int curY = 0;
	//
	// sb.append(positionFormat.format(0));
	// for (Int2D location : s.allLocations()) {
	// if (location.y > curY) {
	// sb.append("\n");
	// curY++;
	// sb.append(positionFormat.format(curY));
	// }
	// sb.append(Utils.padLeft(
	// String.valueOf(s.getNetworks(null, null, location).size()),
	// 4));
	// }
	//
	// return sb;
	// }
	//
	// private StringBuffer printCustomerGrid() {
	// StringBuffer sb = new StringBuffer();
	//
	// int curY = 0;
	//
	// sb.append(NetworkProvider.positionFormat.format(0));
	// for (Int2D location : s.allLocations()) {
	// if (location.y > curY) {
	// sb.append("\n");
	// curY++;
	// sb.append(NetworkProvider.positionFormat.format(curY));
	// }
	// sb.append(Utils.padLeft(
	// String.valueOf(Math.round(this.getCustomers(location))), 7));
	// }
	//
	// return sb;
	// }
	//
	// private StringBuffer printNetworkGrid() {
	// StringBuffer sb = new StringBuffer();
	// DecimalFormat positionFormat = new DecimalFormat("00");
	// DecimalFormat numCustFormat = new DecimalFormat("0000000");
	//
	// int curY = 0;
	//
	// sb.append(positionFormat.format(0));
	// for (Int2D location : s.allLocations()) {
	// if (location.y > curY) {
	// sb.append("\n");
	// curY++;
	// sb.append(positionFormat.format(curY));
	// }
	// sb.append(Utils.padLeft(
	// String.valueOf(this.getNetworks(location).size()), 4));
	// }
	//
	// return sb;
	// }
	//
	// private StringBuffer printPriceGrid() {
	// StringBuffer sb = new StringBuffer();
	// int curY = 0;
	//
	// sb.append(NetworkProvider.positionFormat.format(0));
	// for (Int2D location : s.allLocations()) {
	// if (location.y > curY) {
	// sb.append("\n");
	// curY++;
	// sb.append(NetworkProvider.positionFormat.format(curY));
	// }
	// EdgeNetwork net = (EdgeNetwork) getNetworkAt(EdgeNetwork.class,
	// location);
	// String price;
	// if (net == null) {
	// price = "   N/A";
	// } else {
	// price = NetworkProvider.priceFormat.format(net.getPrice());
	// }
	// sb.append(" " + price);
	// }
	//
	// return sb;
	// }

	/**
	 * NOTE: The order in which this function is called vis-a-vis other agents
	 * is unspecified.
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {

		// if (bankrupt)
		// return;

		ind.buildEdges(s, this);
		ind.priceEdges(s, this);
		ind.manageBackbone(s, this);
		ind.priceBandwidth(s, this);

		// operate our backbone network
		backbone.step(state);

		// operate our edge networks, pay for them
		for (Int2D loc : s.getAllLocations()) {
			if (edgeNetworks[loc.x][loc.y] != null) {
				edgeNetworks[loc.x][loc.y].step(state);
				payForEdge(edgeNetworks[loc.x][loc.y]);
			}
		}

		// Log financials
		if (TraceConfig.financialStatusNSP) {
			TraceConfig.out.println(this + " Financials: " + financials);
		}

		// Log price map
		// if (TraceConfig.NSPPriceTables) {
		// TraceConfig.out.println(this + " Price Map:\n" + printPriceGrid());
		// }

		// Log customer map
		// if (TraceConfig.NSPCustomerTables) {
		// TraceConfig.out.println(this + " Customer Map:\n"
		// + printCustomerGrid());
		// }
		// Log this NSP's Network
		// if (TraceConfig.networking.edgeStatus) {
		// TraceConfig.out.println(this + " Network Map:\n"
		// + printNetworkGrid());
		// }

		// Log ALL NSP's networks
		// if (TraceConfig.networking.edgeStatus) {
		// TraceConfig.out.println("Unified Network Map:\n"
		// + printAllNetworkGrid());
		// }
		// Log edge congestion
		// if (TraceConfig.networking.congestionNSPSummary) {
		// TraceConfig.out.println(edgeUsageReport());
		// }
		// Log edge congestion
		// if (TraceConfig.networking.edgeUsageSummary) {
		// TraceConfig.out.println(edgeUsageReport());
		// }
		//
		// if (financials.getNetWorth() < -10000.0) {
		// goBankrupt();
		// }

	}

	private void payForEdge(EdgeNetwork edgeNetwork) {

	}

	@Override
	public String toString() {
		return "NSP" + id;
	}

}
