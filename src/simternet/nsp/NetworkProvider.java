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

	private static final long		serialVersionUID	= 1L;

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

			// for now, create an infinite link to this network.
			this.getBackboneNetwork().createEgressLinkTo(aen, 1.0E5, RoutingProtocolConfig.NONE);

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
	 * @return
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

		for (Object obj : allNets.objs) {
			Network n = (Network) obj;
			if (n.getClass().equals(net))
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
		Logger.getRootLogger().log(Level.WARN, this + " going bankrupt: " + this.financials);
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

	private StringBuffer printCustomerGrid() {
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
			sb.append(" " + numCustFormat.format(this.getCustomers(location)));
		}

		return sb;
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

		// Log financials
		Logger.getRootLogger().log(Level.INFO, this + " Financials: " + this.financials);

		// Log customer map
		Logger.getRootLogger().log(Level.INFO, this + " Customer Map:\n" + this.printCustomerGrid());

		// Log edge congestion
		Logger.getRootLogger().log(Level.INFO, this.edgeCongestionReport());

		if (this.financials.getNetWorth() < -1.0E6)
			this.goBankrupt();
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public void update() {
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