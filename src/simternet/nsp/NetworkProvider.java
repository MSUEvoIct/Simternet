package simternet.nsp;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;
import simternet.Financials;
import simternet.Simternet;
import simternet.TraceConfig;
import simternet.Utils;
import simternet.application.ApplicationProvider;
import simternet.ecj.problems.HasFinancials;
import simternet.network.Backbone;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.network.RoutingProtocolConfig;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.TemporalHashMap;
import simternet.temporal.TemporalSparseGrid2D;

/**
 * Each instantiation of this class represents a Network Service Provider as an
 * agent. Its behavior is defined by the step() function, which MASON executed
 * once each time step of the model.
 * 
 * @author kkoning
 */
public abstract class NetworkProvider implements Steppable, AsyncUpdate, HasFinancials {

	protected static DecimalFormat							numCustFormat			= new DecimalFormat("0000000");
	protected static DecimalFormat							positionFormat			= new DecimalFormat("00");
	protected static DecimalFormat							priceFormat				= new DecimalFormat("000.00");
	private static final long								serialVersionUID		= 1L;

	protected TemporalHashMap<ApplicationProvider, Double>	aspTransitPrice			= new TemporalHashMap<ApplicationProvider, Double>();
	protected Backbone										backboneNetwork;
	public boolean											bankrupt				= false;

	public Double											deltaRevenue			= 0.0;
	protected TemporalSparseGrid2D							edgeNetworks;
	public Financials										financials;
	protected Int2D											homeBase;
	protected ASPInterconnectPricingStrategy				interconnectStrategy	= null;
	protected InvestmentStrategy							investmentStrategy;
	protected String										name;
	public PricingStrategy									pricingStrategy;

	public Simternet										s						= null;

	public NetworkProvider(Simternet simternet) {
		s = simternet;

		// this.name = simternet.config.getNSPName();

		financials = new Financials(simternet, simternet.config.nspEndowment);

		// Give them a random "home base" somewhere on the grid.
		int homeX = simternet.random.nextInt(simternet.config.gridSize.x);
		int homeY = simternet.random.nextInt(simternet.config.gridSize.y);
		homeBase = new Int2D(homeX, homeY);

		edgeNetworks = new TemporalSparseGrid2D(simternet.config.gridSize.x, simternet.config.gridSize.y);

		backboneNetwork = new Backbone(this);

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
	protected void buildNetwork(Class<? extends EdgeNetwork> type, Int2D location) {
		try {
			Constructor<? extends EdgeNetwork> constr = type.getConstructor(NetworkProvider.class, Int2D.class);

			Double buildCost = EdgeNetwork.getBuildCost(type, this, location);
			EdgeNetwork aen = constr.newInstance(this, location);
			financials.capitalize(buildCost);
			edgeNetworks.setObjectLocation(aen, location);

			// for now, create a fixed bandwidth link to this network.
			getBackboneNetwork().createEgressLinkTo(aen, s.config.nspInitialEdgeNetworkBandwidth,
					RoutingProtocolConfig.NONE);

			if (TraceConfig.NSPBuiltNetwork && Logger.getRootLogger().isTraceEnabled()) {
				Logger.getRootLogger().trace(this + " building " + type + " @ " + location);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String edgeUsageReport() {
		StringBuffer sb = new StringBuffer();
		sb.append("Edge usage for " + this + "\n");
		for (Object o : edgeNetworks) {
			EdgeNetwork aen = (EdgeNetwork) o;
			sb.append(aen + ": ");
			sb.append(aen.getUsageRatio());
			sb.append("\n");
		}
		return sb.toString();
	}

	public Double getASPTransitPrice(ApplicationProvider asp) {
		Double price = aspTransitPrice.get(asp);
		Double min_price = 0.000000000001;
		if (price == null) {
			price = min_price;
		} else if (price.isNaN()) {
			price = min_price;
		} else if (price <= 0.0) {
			price = min_price;
		}
		return price;
	}

	public Backbone getBackboneNetwork() {
		return backboneNetwork;
	}

	/**
	 * @return
	 * 
	 *         Get the total number of customers the provider currently has.
	 */
	@SuppressWarnings("unchecked")
	public Double getCustomers() {
		double numCustomers = 0.0;
		Iterator<EdgeNetwork> allNetworks = edgeNetworks.iterator();
		while (allNetworks.hasNext()) {
			EdgeNetwork aen = allNetworks.next();
			numCustomers += aen.getNumSubscribers();
		}
		return numCustomers;
	}

	/**
	 * @param location
	 * @return The total number of customers at the specified location.
	 */
	@SuppressWarnings("unchecked")
	public Double getCustomers(Int2D location) {
		Double numCustomers = 0.0;

		Bag networks = edgeNetworks.getObjectsAtLocation(location);
		if (networks == null)
			return 0.0;

		Iterator<EdgeNetwork> networksIterator = networks.iterator();

		while (networksIterator.hasNext()) {
			EdgeNetwork aen = networksIterator.next();
			numCustomers += aen.getNumSubscribers();
		}

		return numCustomers;
	}

	/**
	 * Passes back the change in revenue from the previous time step to the
	 * current time step
	 * 
	 * @return change in revenue in units of currency
	 */
	public Double getDeltaRevenue() {
		return deltaRevenue;
	}

	public Collection<EdgeNetwork> getEdgeNetworks() {
		ArrayList<EdgeNetwork> list = new ArrayList<EdgeNetwork>();
		Bag nets = edgeNetworks.allObjects;

		// If there are 0 objects, the Bag will be null rather than empty. :/
		if (null == nets)
			return list;

		for (int i = 0; i < nets.numObjs; i++) {
			list.add((EdgeNetwork) nets.objs[i]);
		}

		return list;
	}

	public Financials getFinancials() {
		return financials;
	}

	public Int2D getHomeBase() {
		return homeBase;
	}

	public String getName() {
		return name;
	}

	public Network getNetworkAt(Class<? extends Network> net, Int2D location) {
		Bag allNets = edgeNetworks.getObjectsAtLocation(location);
		if (allNets == null) // we have no nets at this loc
			return null;
		if (allNets.isEmpty()) // we have no nets at this loc
			return null;

		for (Object obj : allNets) {
			Network n = (Network) obj;
			if (net.isAssignableFrom(n.getClass()))
				return n;
		}
		return null;
	}

	/**
	 * @return All this NSP's networks
	 */
	public Collection<Network> getNetworks() {
		return this.getNetworks(null);
	}

	public Collection<Network> getNetworks(Int2D location) {
		ArrayList<Network> list = new ArrayList<Network>();
		Bag edgeNets;
		if (location != null) {
			edgeNets = edgeNetworks.getObjectsAtLocation(location.x, location.y);
		} else {
			edgeNets = edgeNetworks.allObjects;
		}

		// If there are 0 objects, the Bag will be null rather than empty. :/
		if (edgeNets != null) {
			for (int i = 0; i < edgeNets.numObjs; i++) {
				list.add((Network) edgeNets.objs[i]);
			}
		}

		if (location == null) {
			list.add(getBackboneNetwork());
		}

		return list;
	}

	/**
	 * Extracts the number from the network's name, and returns it
	 * 
	 * @return -1 if number extraction failed, or the number if it succeeded.
	 */
	public int getNumber() {
		int start = name.lastIndexOf('-') + 1;
		try {
			int number = Integer.parseInt(name.substring(start));
			return number;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Dispose of all assets, exit market
	 */
	private void goBankrupt() {
		if (TraceConfig.bankruptcyNSP && Logger.getRootLogger().isTraceEnabled()) {
			Logger.getRootLogger().trace(this + " going bankrupt: " + financials);
		}
		for (EdgeNetwork edge : (Iterable<EdgeNetwork>) edgeNetworks) {
			edge.disconnect();
		}
		edgeNetworks.clear();
		backboneNetwork.disconnect();
		bankrupt = true;
	}

	public boolean hasNetworkAt(Class<? extends Network> net, Int2D location) {
		Network an = getNetworkAt(net, location);
		if (an == null)
			return false;
		else
			return true;
	}

	protected void makeNetworkInvestment() {
		investmentStrategy.makeNetworkInvestment();
	}

	private StringBuffer printAllNetworkGrid() {
		StringBuffer sb = new StringBuffer();
		DecimalFormat positionFormat = new DecimalFormat("00");
		DecimalFormat numCustFormat = new DecimalFormat("0000000");

		int curY = 0;

		sb.append(positionFormat.format(0));
		for (Int2D location : s.allLocations()) {
			if (location.y > curY) {
				sb.append("\n");
				curY++;
				sb.append(positionFormat.format(curY));
			}
			sb.append(Utils.padLeft(String.valueOf(s.getNetworks(null, null, location).size()), 4));
		}

		return sb;
	}

	private StringBuffer printCustomerGrid() {
		StringBuffer sb = new StringBuffer();

		int curY = 0;

		sb.append(NetworkProvider.positionFormat.format(0));
		for (Int2D location : s.allLocations()) {
			if (location.y > curY) {
				sb.append("\n");
				curY++;
				sb.append(NetworkProvider.positionFormat.format(curY));
			}
			sb.append(Utils.padLeft(String.valueOf(Math.round(this.getCustomers(location))), 7));
		}

		return sb;
	}

	private StringBuffer printNetworkGrid() {
		StringBuffer sb = new StringBuffer();
		DecimalFormat positionFormat = new DecimalFormat("00");
		DecimalFormat numCustFormat = new DecimalFormat("0000000");

		int curY = 0;

		sb.append(positionFormat.format(0));
		for (Int2D location : s.allLocations()) {
			if (location.y > curY) {
				sb.append("\n");
				curY++;
				sb.append(positionFormat.format(curY));
			}
			sb.append(Utils.padLeft(String.valueOf(this.getNetworks(location).size()), 4));
		}

		return sb;
	}

	private StringBuffer printPriceGrid() {
		StringBuffer sb = new StringBuffer();
		int curY = 0;

		sb.append(NetworkProvider.positionFormat.format(0));
		for (Int2D location : s.allLocations()) {
			if (location.y > curY) {
				sb.append("\n");
				curY++;
				sb.append(NetworkProvider.positionFormat.format(curY));
			}
			EdgeNetwork net = (EdgeNetwork) getNetworkAt(EdgeNetwork.class, location);
			String price;
			if (net == null) {
				price = "   N/A";
			} else {
				price = NetworkProvider.priceFormat.format(net.getPrice());
			}
			sb.append(" " + price);
		}

		return sb;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * NOTE: The order in which this function is called vis-a-vis other agents
	 * is unspecified.
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {

		if (bankrupt)
			return;

		makeNetworkInvestment();

		pricingStrategy.priceEdges(); // Set prices for the next period.

		// operate our backbone network
		backboneNetwork.step(state);

		// operate our edge networks
		for (Object o : edgeNetworks) {
			EdgeNetwork aen = (EdgeNetwork) o;
			aen.step(state);
		}

		if (Logger.getRootLogger().isTraceEnabled()) {
			// Log financials
			if (TraceConfig.financialStatusNSP) {
				Logger.getRootLogger().trace(this + " Financials: " + financials);
			}

			// Log price map
			if (TraceConfig.NSPPriceTables) {
				Logger.getRootLogger().trace(this + " Price Map:\n" + printPriceGrid());
			}

			// Log customer map
			if (TraceConfig.NSPCustomerTables) {
				Logger.getRootLogger().trace(this + " Customer Map:\n" + printCustomerGrid());
			}
			// Log this NSP's Network
			Logger.getRootLogger().log(Level.INFO, this + " Network Map:\n" + printNetworkGrid());

			// Log ALL NSP's networks
			Logger.getRootLogger().log(Level.INFO, "Unified Network Map:\n" + printAllNetworkGrid());

			// Log edge congestion
			Logger.getRootLogger().log(Level.INFO, edgeUsageReport());

			// Log edge congestion
			if (TraceConfig.congestionNSPSummary) {
				Logger.getRootLogger().trace(edgeUsageReport());
			}

		}

		if (financials.getNetWorth() < -10000.0) {
			goBankrupt();
		}
	}

	@Override
	public String toString() {
		return this.getClass().getCanonicalName() + "-" + name;
	}

	public void update() {

		if (bankrupt)
			return;

		deltaRevenue = financials.getTotalRevenue();
		financials.update();
		// Calculate delta revenue as current revenue - past revenue
		deltaRevenue = financials.getTotalRevenue() - deltaRevenue;

		edgeNetworks.update();
		backboneNetwork.update();
		aspTransitPrice.update();

		for (Object o : edgeNetworks) {
			EdgeNetwork aen = (EdgeNetwork) o;
			aen.update();
		}

	}

}
