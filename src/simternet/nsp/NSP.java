package simternet.nsp;

import java.util.ArrayList;
import java.util.List;

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

	// Reference to the main simulation object
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
	public void buildNetwork(int x, int y) {
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

	public void setASPTransitPrice(int aspID, double price) {
		if (aspTransitPrices == null)
			aspTransitPrices = new double[s.allASPs.length];
		aspTransitPrices[aspID] = limitAspTransitPrice(price);
	}

	/**
	 * @return the total number of customers the provider currently has.
	 */
	public double getCustomers() {
		double numCustomers = 0.0;
		for (Consumer c : s.allConsumers) {
			numCustomers += c.getNSPSubscribers(id);
		}
		return numCustomers;
	}

	/**
	 * @return The total number of customers at the specified location.
	 */
	public double getCustomers(int x, int y) {
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

	public boolean hasNetworkAt(int x, int y) {
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

		// Possibly Build EdgeNetworks
		for (EdgeBuildingStimulus ebs : makeEdgeBuildingStimuli()) {
			boolean build = ind.buildEdge(ebs);
			if (build)
				buildNetwork(ebs.location.x, ebs.location.y);
		}

		// Price the EdgeNetworks we've built
		for (EdgePricingStimulus eps : makeEdgePricingStimuli()) {
			double price = ind.priceEdge(eps);
			setEdgePrice(eps.location.x, eps.location.y, price);
		}

		// Price ASP BackboneLink connections
		for (BackbonePricingStimulus bps : makeBackbonePricingStimuli()) {
			double price = ind.priceBackboneLink(bps);
			setASPTransitPrice(bps.aspID, price);
		}

		// operate our backbone network
		backbone.step(state);

		// operate our edge networks, pay for them
		for (Int2D loc : s.getAllLocations()) {
			if (edgeNetworks[loc.x][loc.y] != null) {
				edgeNetworks[loc.x][loc.y].step(state);
				payForEdge(loc.x, loc.y);
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

	private List<EdgeBuildingStimulus> makeEdgeBuildingStimuli() {
		ArrayList<EdgeBuildingStimulus> stimuli = new ArrayList<EdgeBuildingStimulus>();
		for (Int2D loc : s.getAllLocations()) {
			if (edgeNetworks[loc.x][loc.y] == null) {
				// Then we don't already have a network here
				EdgeBuildingStimulus ebs = new EdgeBuildingStimulus();
				ebs.location = loc;
				ebs.numEdges = s.getNumEdges(loc.x, loc.y);
				ebs.random = s.random;
				stimuli.add(ebs);
			}
		}
		return stimuli;
	}

	private List<EdgePricingStimulus> makeEdgePricingStimuli() {
		ArrayList<EdgePricingStimulus> stimuli = new ArrayList<EdgePricingStimulus>();

		for (Int2D loc : s.getAllLocations()) {
			if (edgeNetworks[loc.x][loc.y] != null) {
				// Then we have a network here
				EdgePricingStimulus eps = new EdgePricingStimulus();
				eps.location = loc;
				eps.numEdges = s.getNumEdges(loc.x, loc.y);
				eps.random = s.random;
				eps.currentPrice = this.edgeNetworks[loc.x][loc.y].price;
				eps.minPrice = eps.currentPrice;
				eps.minOtherPrice = 200; // TODO, hard coded max; currently very
											// reasonable

				for (int i = 0; i < s.allNSPs.length; i++) {
					if (s.allNSPs[i].edgeNetworks[loc.x][loc.y] != null) {
						if (s.allNSPs[i].edgeNetworks[loc.x][loc.y].price < eps.minPrice) {
							eps.minPrice = s.allNSPs[i].edgeNetworks[loc.x][loc.y].price;
						}
						if (i != this.id) {
							if (s.allNSPs[i].edgeNetworks[loc.x][loc.y].price < eps.minOtherPrice) {
								eps.minOtherPrice = s.allNSPs[i].edgeNetworks[loc.x][loc.y].price;
							}
						}

					}
				}

				double totalPopulation = Double.MIN_NORMAL;
				totalPopulation += s.getTotalPopulation(loc.x, loc.y);
				double mySubs = this.getCustomers(loc.x, loc.y);
				double allSubs = Double.MIN_NORMAL;
				for (int nspID = 0; nspID < s.allNSPs.length; nspID++) {
					allSubs += s.allNSPs[nspID].getCustomers(loc.x, loc.y);
				}

				eps.percentOfPopulation = mySubs / totalPopulation;
				eps.percentOfSubscriptions = mySubs / allSubs;

				stimuli.add(eps);
			}
		}
		return stimuli;
	}

	private List<BackbonePricingStimulus> makeBackbonePricingStimuli() {
		ArrayList<BackbonePricingStimulus> stimuli = new ArrayList<BackbonePricingStimulus>();
		for (int aspID = 0; aspID < s.allASPs.length; aspID++) {
			BackbonePricingStimulus bps = new BackbonePricingStimulus();
			bps.aspID = aspID;
			stimuli.add(bps);
		}
		return stimuli;
	}

	private void payForEdge(int x, int y) {
		// only pay if we have an edge here
		if (edgeNetworks[x][y] != null) {
			double cost = s.edgeOpCostFixed;
			cost += s.edgeOpCostPerUser * getCustomers(x, y);
			financials.payExpense(cost);
		}
	}

	@Override
	public String toString() {
		return "NSP" + id;
	}

	private void setEdgePrice(int x, int y, double price) {
		if (price < Double.MIN_NORMAL)
			price = Double.MIN_NORMAL;
		edgeNetworks[x][y].price = price;
	}

}
