package simternet.nsp;

import java.util.Iterator;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;
import simternet.Exogenous;
import simternet.Simternet;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;
import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;
import simternet.temporal.TemporalSparseGrid2D;

/**
 * @author kkoning
 * 
 *         Each instantiation of this class represents a Network Service
 *         Provider as an agent. Its behavior is defined by the step() function,
 *         which MASON executed once each time step of the model.
 * 
 */
public abstract class AbstractNetworkProvider implements Steppable, AsyncUpdate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Int2D homeBase;
	protected InvestmentStrategy investmentStrategy;
	protected Investor investor;
	protected Temporal<Double> liquidAssets;
	protected TemporalSparseGrid2D networks;
	protected Temporal<Double> periodCosts = new Temporal<Double>(0.0, 0.0);
	protected Temporal<Double> periodInvestment = new Temporal<Double>(0.0, 0.0);
	protected Temporal<Double> periodRevenue = new Temporal<Double>(0.0, 0.0);
	protected PricingStrategy pricingStrategy;
	protected Simternet simternet = null;
	protected Temporal<Double> totalCosts = new Temporal<Double>(0.0);
	protected Temporal<Double> totalInvested = new Temporal<Double>(0.0);
	protected Temporal<Double> totalRevenue = new Temporal<Double>(0.0);

	public AbstractNetworkProvider(Simternet s) {
		this(s, null);
	}

	public AbstractNetworkProvider(Simternet s, Investor i) {
		this.simternet = s;
		this.liquidAssets = new Temporal<Double>(Exogenous.nspEndowment);
		int homeX = s.random.nextInt(Exogenous.landscapeX);
		int homeY = s.random.nextInt(Exogenous.landscapeY);
		this.homeBase = new Int2D(homeX, homeY);
		this.networks = new TemporalSparseGrid2D(Exogenous.landscapeX,
				Exogenous.landscapeY);
		if (i != null)
			this.investor = i;
		else
			this.investor = new Investor(this);
	}

	/**
	 * This method loops through all the networks a provider owns and bills the
	 * customers who are currently subscribed to the network.
	 */
	@SuppressWarnings("unchecked")
	protected void billCustomers() {
		Iterator<AbstractNetwork> nets = this.networks.iterator();
		while (nets.hasNext()) {
			AbstractNetwork an = nets.next();
			Double revenue = an.billCustomers();
			this.earn(revenue);
		}
	}

	/**
	 * @param an
	 *            The network to be built
	 * 
	 *            A network object may be instantiated, but it is not considered
	 *            "built" until it hits this method. This method is responsible
	 *            for financing the network and placing it in the list of
	 *            networks owned by this network service provider.
	 */
	protected void buildNetwork(AbstractNetwork an) {
		this.capitalize(an.getBuildCost());
		this.networks.setObjectLocation(an, an.getLocationX(), an
				.getLocationY());
	}

	protected void buildNetwork(Class<? extends AbstractNetwork> net,
			Integer x, Integer y) {
		try {
			AbstractNetwork an = net.newInstance();
			an.init(this, x, y);
			this.buildNetwork(an);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param buildCost
	 * 
	 */
	private void capitalize(Double buildCost) {
		this.investor.finance(buildCost);
		this.periodInvestment
				.set(this.periodInvestment.getFuture() + buildCost);
		this.totalInvested.set(this.totalInvested.getFuture() + buildCost);
	}

	protected void earn(Double revenue) {
		this.liquidAssets.set(this.liquidAssets.getFuture() + revenue);
		this.totalRevenue.set(this.totalRevenue.getFuture() + revenue);
		this.periodRevenue.set(this.periodRevenue.getFuture() + revenue);
	}

	/**
	 * @return
	 * 
	 *         Get the total number of customers the provider currently has.
	 */
	public Double getCustomers() {
		Double numCustomers = 0.0;
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				numCustomers += this.getCustomers(x, y);
		return numCustomers;
	}

	public Double getCustomers(AbstractConsumerClass ac) {
		Double numCustomers = 0.0;
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				numCustomers += this.getCustomers(ac, x, y);
		return numCustomers;
	}

	/**
	 * @param ac
	 * @param x
	 * @param y
	 * @return The number of subscriptions from this consumer group at this
	 *         location. Because each member of the consumer group may subscribe
	 *         to more than one service
	 */
	public Double getCustomers(AbstractConsumerClass ac, Integer x, Integer y) {
		Double numCustomers = 0.0;
		for (AbstractNetwork n : (AbstractNetwork[]) this.networks
				.getObjectsAtLocation(x, y).objs)
			numCustomers += n.getCustomers(ac);
		return numCustomers;
	}

	/**
	 * @param network
	 * @param ac
	 * @param x
	 * @param y
	 * @return How many people from the specified consumer group subscribe to
	 *         the specified network at the specified location.
	 */
	@SuppressWarnings("unchecked")
	public Double getCustomers(Class network, AbstractConsumerClass ac,
			Integer x, Integer y) {
		Bag b = this.networks.getObjectsAtLocation(x, y);
		if (b == null)
			return 0.0;
		Object[] objs = b.objs;
		for (Object obj : objs) {
			if (obj == null)
				continue;
			AbstractNetwork n = (AbstractNetwork) obj;
			if (network.isInstance(n))
				return n.getCustomers(ac);
		}
		return 0.0;
	}

	/**
	 * @param network
	 * @param x
	 * @param y
	 * @return The total number of customers subscribing to the network from all
	 *         consumer classes at the specified location.
	 */
	@SuppressWarnings("unchecked")
	public Double getCustomers(Class network, Integer x, Integer y) {
		for (AbstractNetwork n : (AbstractNetwork[]) this.networks
				.getObjectsAtLocation(x, y).objs)
			if (network.isInstance(n))
				return n.getTotalCustomers();
		return 0.0;
	}

	/**
	 * @param x
	 * @param y
	 * @return Get the total number of customers at the specified location.
	 */
	public Double getCustomers(Integer x, Integer y) {
		Double numCustomers = 0.0;
		if (this.networks.getObjectsAtLocation(x, y) == null)
			return numCustomers;
		for (int i = 0; i < this.networks.getObjectsAtLocation(x, y).size(); i++)
			if (this.networks.getObjectsAtLocation(x, y).get(i) != null)
				numCustomers += ((AbstractNetwork) this.networks.allObjects
						.get(i)).getTotalCustomers();
		// TODO: Why didn't the below code work? Suspicion: Bags suck.
		// for (AbstractNetwork n : (AbstractNetwork[]) this.networks
		// .getObjectsAtLocation(x, y).objs)
		// if (n != null)
		// numCustomers += n.getTotalCustomers();
		return numCustomers;
	}

	public Double getDebt() {
		return this.investor.balance;
	}

	public Int2D getHomeBase() {
		return this.homeBase;
	}

	public Double getLiquidAssets() {
		return this.liquidAssets.get();
	}

	public AbstractNetwork getNetworkAt(Class<? extends AbstractNetwork> net,
			int x, int y) {
		Bag nets = this.networks.getObjectsAtLocation(x, y); // All of our nets
		// at
		// this loc
		if (nets == null) // we have no nets at this loc
			return null;
		if (nets.isEmpty()) // we have no nets at this loc
			return null;

		for (Object obj : nets.objs) {
			AbstractNetwork n = (AbstractNetwork) obj;
			if (n.getClass().equals(net))
				return n;
		}
		return null;
	}

	public Double getOnePeriodCosts() {
		return this.periodCosts.get();
	}

	public Double getOnePeriodRevenue() {
		return this.periodRevenue.get();
	}

	/**
	 * @param cl
	 * @param x
	 * @param y
	 * @return The price set by the network service provider for this specific
	 *         network at this location. This method should be overridden if
	 *         NSPs to not set prices in individual network objects.
	 */
	public Double getPrice(Class<? extends AbstractNetwork> cl,
			AbstractConsumerClass cc, int x, int y) {
		return this.pricingStrategy.getPrice(cl, cc, x, y);
	}

	/**
	 * @return The simulation in which this network service provider is
	 *         participating.
	 * 
	 *         This method exists as a memory-saving convienence, so that other
	 *         objects (i.e., networks) need not store a reference to both their
	 *         owning NSP -and- the simternet object. This saves memory at least
	 *         equal to sizeof(pointer) * #NSPs * Avg. networks per NSP.
	 */
	public Simternet getSimternet() {
		return this.simternet;
	}

	public Double getTotalRevenueCollected() {
		return this.totalRevenue.get();
	}

	public boolean hasNetworkAt(Class<? extends AbstractNetwork> net,
			Integer x, Integer y) {
		AbstractNetwork an = this.getNetworkAt(net, x, y);
		if (an == null)
			return false;
		else
			return true;
	}

	protected void makeNetworkInvestment() {
		this.investmentStrategy.makeNetworkInvestment();
	}

	@SuppressWarnings("unchecked")
	private void printInfo() {
		System.out.println("NSP: " + this.getClass().getSimpleName() + "-"
				+ this.hashCode() + ", totalRev = " + this.totalRevenue.get()
				+ ", liquidAssets = " + this.liquidAssets.get());

		System.out.print("  XxY=#cust@price: ");

		Iterator<AbstractNetwork> nets = this.networks.allObjects.iterator();
		while (nets.hasNext()) {
			AbstractNetwork an = nets.next();
			System.out.print(an.getLocationX() + "x" + an.getLocationY() + "=");
			System.out.print(an.getTotalCustomers() + "@"
					+ an.getPrice(null).toString().substring(0, 4));
			if ((Exogenous.landscapeX * Exogenous.landscapeY) > 15)
				System.out.print("\n");
			else if (nets.hasNext() == true)
				System.out.print(", ");
		}
		System.out.print("\n");

	}

	private void serviceDebt() {
		Double amountOwed = this.investor.getPayment();
		Double amountToPay;
		if (amountOwed > this.liquidAssets.getFuture())
			amountToPay = this.liquidAssets.getFuture();
		else
			amountToPay = amountOwed;

		this.investor.makePayment(amountToPay);
		this.liquidAssets.set(this.liquidAssets.getFuture() - amountToPay);
	}

	public void setCustomers(Class<? extends AbstractNetwork> network,
			AbstractConsumerClass ac, Integer x, Integer y, Double numCustomers) {
		Bag b = this.networks.getObjectsAtLocation(x, y);
		if (b == null)
			throw new RuntimeException(
					"Setting customers at a location with no networks.");

		for (Object obj : this.networks.getObjectsAtLocation(x, y).objs) {
			AbstractNetwork n = (AbstractNetwork) obj;
			if (network.isInstance(n))
				n.setCustomers(ac, numCustomers);
		}
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
		/*
		 * Investors are stepped here, rather than in the main loop, because of
		 * the fixed 1:1 correspondence between investors and NSPs.
		 */
		this.investor.step(state);

		this.serviceDebt();
		this.makeNetworkInvestment();

		this.billCustomers(); // Bill customers who are subscribed this period
		this.setPrices(); // Set prices for the next period.
		if (this.simternet.debug == true)
			this.printInfo();

	}

	public void update() {
		this.liquidAssets.update();

		this.totalCosts.update();
		this.totalInvested.update();
		this.totalRevenue.update();

		this.periodCosts.update();
		this.periodInvestment.update();
		this.periodRevenue.update();

		this.networks.update();
	}

}
