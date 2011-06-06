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
import simternet.network.Backbone;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.network.RoutingProtocolConfig;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.TemporalSparseGrid2D;

/**
 * Each instantiation of this class represents a Network Service Provider as an
 * agent. Its behavior is defined by the step() function, which MASON executed
 * once each time step of the model.
 * 
 * @author kkoning
 */
public abstract class NetworkProvider implements Steppable, AsyncUpdate {

	protected Backbone				backboneNetwork;
	public boolean					bankrupt			= false;
	public Double					deltaRevenue		= 0.0;
	protected TemporalSparseGrid2D	edgeNetworks;

	public Financials				financials;
	protected Int2D					homeBase;
	protected InvestmentStrategy	investmentStrategy;
	protected String				name;
	public PricingStrategy			pricingStrategy;
	public Simternet				simternet			= null;
	protected static DecimalFormat	numCustFormat		= new DecimalFormat("0000000");
	protected static DecimalFormat	positionFormat		= new DecimalFormat("00");
	protected static DecimalFormat	priceFormat			= new DecimalFormat("000.00");

	private static final long		serialVersionUID	= 1L;

	public NetworkProvider(Simternet simternet) {
		this.simternet = simternet;

		this.name = simternet.config.getNSPName();

		Double endowment = Double.parseDouble(this.simternet.config.getProperty("nsp.financial.endowment"));
		this.financials = new Financials(simternet, endowment);

		int homeX = simternet.random.nextInt(this.simternet.config.x());
		int homeY = simternet.random.nextInt(this.simternet.config.y());
		this.homeBase = new Int2D(homeX, homeY);

		this.edgeNetworks = new TemporalSparseGrid2D(this.simternet.config.x(), this.simternet.config.y());

		this.backboneNetwork = new Backbone(this);

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
			this.financials.capitalize(buildCost);
			this.edgeNetworks.setObjectLocation(aen, location);

			// for now, create a fixed bandwidth link to this network.
			this.getBackboneNetwork().createEgressLinkTo(aen, 1.0E5, RoutingProtocolConfig.NONE);

			if (TraceConfig.NSPBuiltNetwork && Logger.getRootLogger().isTraceEnabled())
				Logger.getRootLogger().trace(this + " building " + type + " @ " + location);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String edgeCongestionReport() {
		StringBuffer sb = new StringBuffer();
		sb.append("Edge congestion for " + this + "\n");
		for (Object o : this.edgeNetworks) {
			EdgeNetwork aen = (EdgeNetwork) o;
			sb.append(aen + ": ");
			sb.append(aen.getCongestionReport());
			sb.append("\n");
		}
		return sb.toString();
	}

	public Backbone getBackboneNetwork() {
		return this.backboneNetwork;
	}

	/**
	 * @return
	 * 
	 *         Get the total number of customers the provider currently has.
	 */
	@SuppressWarnings("unchecked")
	public Double getCustomers() {
		double numCustomers = 0.0;
		Iterator<EdgeNetwork> allNetworks = this.edgeNetworks.iterator();
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

		Bag networks = this.edgeNetworks.getObjectsAtLocation(location);
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
		return this.deltaRevenue;
	}

	public Collection<EdgeNetwork> getEdgeNetworks() {
		ArrayList<EdgeNetwork> list = new ArrayList<EdgeNetwork>();
		Bag nets = this.edgeNetworks.allObjects;

		// If there are 0 objects, the Bag will be null rather than empty. :/
		if (null == nets)
			return list;

		for (int i = 0; i < nets.numObjs; i++)
			list.add((EdgeNetwork) nets.objs[i]);

		return list;
	}

	public Int2D getHomeBase() {
		return this.homeBase;
	}

	public String getName() {
		return this.name;
	}

	public Network getNetworkAt(Class<? extends Network> net, Int2D location) {
		Bag allNets = this.edgeNetworks.getObjectsAtLocation(location);
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
		if (location != null)
			edgeNets = this.edgeNetworks.getObjectsAtLocation(location.x, location.y);
		else
			edgeNets = this.edgeNetworks.allObjects;

		// If there are 0 objects, the Bag will be null rather than empty. :/
		if (edgeNets != null)
			for (int i = 0; i < edgeNets.numObjs; i++)
				list.add((Network) edgeNets.objs[i]);

		if (location == null)
			list.add(this.getBackboneNetwork());

		return list;
	}

	/**
	 * Dispose of all assets, exit market
	 */
	private void goBankrupt() {
		if (TraceConfig.bankruptcyNSP && Logger.getRootLogger().isTraceEnabled())
			Logger.getRootLogger().trace(this + " going bankrupt: " + this.financials);
		for (EdgeNetwork edge : (Iterable<EdgeNetwork>) this.edgeNetworks)
			edge.disconnect();
		this.edgeNetworks.clear();
		this.backboneNetwork.disconnect();
		this.bankrupt = true;
	}

	public boolean hasNetworkAt(Class<? extends Network> net, Int2D location) {
		Network an = this.getNetworkAt(net, location);
		if (an == null)
			return false;
		else
			return true;
	}

	protected void makeNetworkInvestment() {
		this.investmentStrategy.makeNetworkInvestment();
	}

	private StringBuffer printAllNetworkGrid() {
		StringBuffer sb = new StringBuffer();
		DecimalFormat positionFormat = new DecimalFormat("00");
		DecimalFormat numCustFormat = new DecimalFormat("0000000");

		int curY = 0;

		sb.append(positionFormat.format(0));
		for (Int2D location : this.simternet.allLocations()) {
			if (location.y > curY) {
				sb.append("\n");
				curY++;
				sb.append(positionFormat.format(curY));
			}
			sb.append(Utils.padLeft(String.valueOf(this.simternet.getNetworks(null, null, location).size()), 4));
		}

		return sb;
	}

	private StringBuffer printCustomerGrid() {
		StringBuffer sb = new StringBuffer();

		int curY = 0;

		sb.append(NetworkProvider.positionFormat.format(0));
		for (Int2D location : this.simternet.allLocations()) {
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
		for (Int2D location : this.simternet.allLocations()) {
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
		for (Int2D location : this.simternet.allLocations()) {
			if (location.y > curY) {
				sb.append("\n");
				curY++;
				sb.append(NetworkProvider.positionFormat.format(curY));
			}
			EdgeNetwork net = (EdgeNetwork) this.getNetworkAt(EdgeNetwork.class, location);
			String price;
			if (net == null)
				price = "   N/A";
			else
				price = NetworkProvider.priceFormat.format(net.getPrice());
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

		if (this.bankrupt)
			return;

		this.makeNetworkInvestment();

		this.pricingStrategy.priceEdges(); // Set prices for the next period.

		// operate our backbone network
		this.backboneNetwork.step(state);

		// operate our edge networks
		for (Object o : this.edgeNetworks) {
			EdgeNetwork aen = (EdgeNetwork) o;
			aen.step(state);
		}

		if (Logger.getRootLogger().isTraceEnabled()) {
			// Log financials
			if (TraceConfig.financialStatusNSP)
				Logger.getRootLogger().trace(this + " Financials: " + this.financials);

			// Log price map
			if (TraceConfig.NSPPriceTables)
				Logger.getRootLogger().trace(this + " Price Map:\n" + this.printPriceGrid());

			// Log customer map
			if (TraceConfig.NSPCustomerTables)
				Logger.getRootLogger().trace(this + " Customer Map:\n" + this.printCustomerGrid());
			// Log this NSP's Network
			Logger.getRootLogger().log(Level.INFO, this + " Network Map:\n" + this.printNetworkGrid());

			// Log ALL NSP's networks
			Logger.getRootLogger().log(Level.INFO, "Unified Network Map:\n" + this.printAllNetworkGrid());

			// Log edge congestion
			Logger.getRootLogger().log(Level.INFO, this.edgeCongestionReport());

			// Log edge congestion
			if (TraceConfig.congestionNSPSummary)
				Logger.getRootLogger().trace(this.edgeCongestionReport());

		}

		if (this.financials.getNetWorth() < -10000.0)
			this.goBankrupt();
	}

	@Override
	public String toString() {
		return this.getClass().getCanonicalName() + "-" + this.name;
	}

	public void update() {

		if (this.bankrupt)
			return;

		this.deltaRevenue = this.financials.getTotalRevenue();
		this.financials.update();
		// Calculate delta revenue as current revenue - past revenue
		this.deltaRevenue = this.financials.getTotalRevenue() - this.deltaRevenue;

		this.edgeNetworks.update();
		this.backboneNetwork.update();

		for (Object o : this.edgeNetworks) {
			EdgeNetwork aen = (EdgeNetwork) o;
			aen.update();
		}

	}

}
