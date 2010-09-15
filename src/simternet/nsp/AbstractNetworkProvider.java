package simternet.nsp;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;
import simternet.Financials;
import simternet.Simternet;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;
import simternet.network.BackboneNetwork;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.TemporalSparseGrid2D;

/**
 * Each instantiation of this class represents a Network Service Provider as an
 * agent. Its behavior is defined by the step() function, which MASON executed
 * once each time step of the model.
 * 
 * @author kkoning
 */
public abstract class AbstractNetworkProvider implements Steppable, AsyncUpdate {

	private static final long serialVersionUID = 1L;

	protected BackboneNetwork backboneNetwork;
	protected TemporalSparseGrid2D edgeNetworks;
	public Financials financials;
	protected Int2D homeBase;
	protected InvestmentStrategy investmentStrategy;
	protected String name = UUID.randomUUID().toString();
	protected PricingStrategy pricingStrategy;
	public Simternet simternet = null;

	public AbstractNetworkProvider(Simternet simternet) {
		this.simternet = simternet;
		this.name = simternet.parameters.getNSPName();

		Double endowment = Double.parseDouble(this.simternet.parameters
				.getProperty("nsp.financial.endowment"));
		this.financials = new Financials(simternet, endowment);

		int homeX = simternet.random.nextInt(this.simternet.parameters.x());
		int homeY = simternet.random.nextInt(this.simternet.parameters.y());
		this.homeBase = new Int2D(homeX, homeY);

		this.edgeNetworks = new TemporalSparseGrid2D(this.simternet.parameters
				.x(), this.simternet.parameters.y());

		this.backboneNetwork = new BackboneNetwork(this);

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
	protected void buildNetwork(Class<? extends AbstractEdgeNetwork> type,
			Int2D location) {
		try {
			Constructor<? extends AbstractEdgeNetwork> constr = type
					.getConstructor(AbstractNetworkProvider.class, Int2D.class);

			Double buildCost = AbstractEdgeNetwork.getBuildCost(type, this,
					location);
			AbstractEdgeNetwork aen = constr.newInstance(this, location);
			this.financials.capitalize(buildCost);
			this.edgeNetworks.setObjectLocation(aen, location);

			// for now, create an infinite link to this network.
			this.getBackboneNetwork().createEgressLinkTo(aen, null);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public BackboneNetwork getBackboneNetwork() {
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
		Iterator<AbstractEdgeNetwork> allNetworks = this.edgeNetworks
				.iterator();
		while (allNetworks.hasNext()) {
			AbstractEdgeNetwork aen = allNetworks.next();
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

		Iterator<AbstractEdgeNetwork> networksIterator = networks.iterator();

		while (networksIterator.hasNext()) {
			AbstractEdgeNetwork aen = networksIterator.next();
			numCustomers += aen.getNumSubscribers();
		}

		return numCustomers;
	}

	public Collection<AbstractNetwork> getEdgeNetworks() {
		ArrayList<AbstractNetwork> list = new ArrayList<AbstractNetwork>();
		Bag nets = this.edgeNetworks.allObjects;

		// If there are 0 objects, the Bag will be null rather than empty. :/
		if (null == nets)
			return list;

		for (int i = 0; i < nets.numObjs; i++)
			list.add((AbstractEdgeNetwork) nets.objs[i]);

		return list;
	}

	public Int2D getHomeBase() {
		return this.homeBase;
	}

	public String getName() {
		return this.name;
	}

	public AbstractNetwork getNetworkAt(Class<? extends AbstractNetwork> net,
			Int2D location) {
		Bag allNets = this.edgeNetworks.getObjectsAtLocation(location);
		if (allNets == null) // we have no nets at this loc
			return null;
		if (allNets.isEmpty()) // we have no nets at this loc
			return null;

		for (Object obj : allNets.objs) {
			AbstractNetwork n = (AbstractNetwork) obj;
			if (n.getClass().equals(net))
				return n;
		}
		return null;
	}

	public Collection<AbstractNetwork> getNetworks(Int2D location) {
		ArrayList<AbstractNetwork> list = new ArrayList<AbstractNetwork>();
		Bag localNets = this.edgeNetworks.getObjectsAtLocation(location.x,
				location.y);

		// If there are 0 objects, the Bag will be null rather than empty. :/
		if (null == localNets)
			return list;

		for (int i = 0; i < localNets.numObjs; i++)
			list.add((AbstractNetwork) localNets.objs[i]);

		return list;
	}

	/**
	 * Determines the price given the following parameters. Many of these
	 * parameters could be ignored, either by regulatory decree or otherwise.
	 * 
	 * @param cl
	 *            The type of network (i.e. Wired/Wireless, DSL/Cable)
	 * @param acc
	 *            The class of consumers
	 * @param location
	 *            The location on the network
	 * @return The price the user will pay
	 */
	public Double getPrice(Class<? extends AbstractNetwork> cl,
			AbstractConsumerClass acc, Int2D location) {
		return this.pricingStrategy.getPrice(cl, null, location);
	}

	public boolean hasNetworkAt(Class<? extends AbstractNetwork> net,
			Int2D location) {
		AbstractNetwork an = this.getNetworkAt(net, location);
		if (an == null)
			return false;
		else
			return true;
	}

	protected void makeNetworkInvestment() {
		this.investmentStrategy.makeNetworkInvestment();
	}

	@SuppressWarnings("unchecked")
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

	protected void setPrices() {
		this.pricingStrategy.setPrices();
	}

	/**
	 * NOTE: The order in which this function is called vis-a-vis other agents
	 * is unspecified.
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {

		this.makeNetworkInvestment();

		this.setPrices(); // Set prices for the next period.
		if (this.simternet.parameters.debugLevel() > 0) {
			System.out.println("Stepping " + this.getName() + ", has "
					+ this.financials);
			System.out.println(this.printCustomerGrid());
		}
		this.backboneNetwork.step(state);
		for (Object o : this.edgeNetworks) {
			AbstractEdgeNetwork aen = (AbstractEdgeNetwork) o;
			aen.step(state);
		}
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public void update() {
		this.financials.update();
		this.edgeNetworks.update();
		this.backboneNetwork.update();
	}

}
