package simternet;

import java.util.Map.Entry;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

/**
 * @author kkoning
 * 
 * Each instantiation of this class represents a Network Service Provider
 * as an agent.  Its behavior is defined by the step() function, which MASON
 * executed once each time step of the model.
 *
 */
public abstract class AbstractNetworkProvider implements Steppable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Double debt = 0.0;
	protected Double liquidAssets = 0.0;
	protected SparseGrid2D networks;
	protected Simternet s = null;
	protected Double totalCapitalExpenditures = 0.0;
	protected Double totalInterestPaid = 0.0;
	protected Double totalRevenueCollected = 0.0;

	public AbstractNetworkProvider(Simternet s) {
		this.s = s;
		liquidAssets += Exogenous.nspEndowment;
		networks = new SparseGrid2D(Exogenous.landscapeX,
				Exogenous.landscapeY);
	}

	protected void billCustomers() {
		// Iterate through all the networks this provider owns
		for(Object obj : networks.allObjects.objs) {
			if (obj == null) continue;
			AbstractNetwork n = (AbstractNetwork) obj;
			for (Entry<AbstractConsumer,Double> e : n.getCustomers().entrySet()) {
				AbstractConsumer cc = e.getKey();
				Double numCustomers = e.getValue();
				Double price = this.getPrice(n.getClass(), cc, n.locationX, n.locationY);
				Double revenue = numCustomers * price;
				earn(revenue);
			}
		}
	}

	/**
	 * @return
	 * 
	 * Get the total number of customers the provider currently has.
	 */
	protected Double getCustomers() {
		Double numCustomers = 0.0;
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				numCustomers += this.getCustomers(x,y);
		return numCustomers;
	}
	
	public boolean hasNetworkAt(Class network, int x, int y) {
		
		Bag nets = networks.getObjectsAtLocation(x, y);
		if (nets == null) return false;
		if (nets.isEmpty()) return false;
		
		for(Object obj : nets.objs) {
			AbstractNetwork n = (AbstractNetwork) obj;
			if (n.getClass().equals(network)) return true;
		}
		
		return false;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return Get the total number of customers at the specified location.
	 */
	protected Double getCustomers(Integer x, Integer y) {
		Double numCustomers = 0.0;
		for(AbstractNetwork n : (AbstractNetwork[]) networks.getObjectsAtLocation(x, y).objs) {
			numCustomers += n.getTotalCustomers();
		}
		return numCustomers;
	}
	
	protected Double getCustomers(AbstractConsumer ac) {
		Double numCustomers = 0.0;
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				numCustomers += this.getCustomers(ac,x,y);
		return numCustomers;
	}
	
	/**
	 * @param ac
	 * @param x
	 * @param y
	 * @return The number of subscriptions from this consumer group at this location.
	 * Because each member of the consumer group may subscribe to more than one service
	 */
	protected Double getCustomers(AbstractConsumer ac, Integer x, Integer y) {
		Double numCustomers = 0.0;
		for(AbstractNetwork n : (AbstractNetwork[]) networks.getObjectsAtLocation(x, y).objs) {
			numCustomers += n.getCustomers(ac);
		}
		return numCustomers;
	}
	
	/**
	 * @param network
	 * @param x
	 * @param y
	 * @return The total number of customers subscribing to the network from
	 * all consumer classes at the specified location.
	 */
	@SuppressWarnings("unchecked")
	protected Double getCustomers(Class network, Integer x, Integer y) {
		for(AbstractNetwork n : (AbstractNetwork[]) networks.getObjectsAtLocation(x, y).objs) {
			if (network.isInstance(n)) {
				return n.getTotalCustomers();
			}
		}
		return 0.0;
	}
	
	/**
	 * @param network
	 * @param ac
	 * @param x
	 * @param y
	 * @return How many people from the specified consumer group subscribe 
	 * to the specified network at the specified location.
	 */
	@SuppressWarnings("unchecked")
	protected Double getCustomers(Class network, AbstractConsumer ac, Integer x, Integer y) {
		for(AbstractNetwork n : (AbstractNetwork[]) networks.getObjectsAtLocation(x, y).objs) {
			if (network.isInstance(n)) {
				return n.getCustomers(ac);
			}
		}
		return 0.0;
	}
	
	protected void setCustomers(Class network, AbstractConsumer ac, Integer x, Integer y, Double numCustomers) {
		Bag b = networks.getObjectsAtLocation(x,y);
		if (b == null)
		
		for(Object obj : networks.getObjectsAtLocation(x, y).objs) {
			AbstractNetwork n = (AbstractNetwork) obj;
			if (network.isInstance(n)) {
				n.setCustomers(ac, numCustomers);
			}
		}
	}
	
	
	
	/**
	 * @param network
	 * 
	 * Utility method:  build the specified network wherever the population is
	 * at least half that of @see Exogenous.maxPopulation .
	 */
	@SuppressWarnings("unchecked")
	protected void buildAtHalfMaxPop(Class network) {
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				if (s.getPopulation(x,y) >= (Exogenous.maxPopulation / 2))
					buildNetwork(network, x, y);
	}

	/**
	 * @param network
	 * 
	 * Utility method:  build the specified network everywhere.
	 * 
	 */
	protected void buildEverywhere(Class network) {
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				buildNetwork(network, x, y);
	}

	/**
	 * The NSP has decided to build a network at x,y.  Build the network
	 * and increase the NSP's debt by the cost.
	 * 
	 * Future: verify provider is not building network twice?
	 */
	private void buildNetwork(Class cl, Integer x, Integer y) {
		try {
			AbstractNetwork network = (AbstractNetwork) cl.newInstance(); // Create a new network, but of the specified type.
			network.init(this, x, y); // give this network information about its position and owner.
			Double buildCost = network.getBuildCost(); 
			networks.setObjectLocation(network, x, y); // Add the network to our collection.
			capitalize(buildCost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void capitalize(Double buildCost) {
		this.debt += buildCost;
		this.totalCapitalExpenditures += buildCost;
	
	}
	
	private void earn(Double revenue) {
		this.liquidAssets += revenue;
		this.totalRevenueCollected += revenue;
	}

	public Double getDebt() {
		return debt;
	}

	public Double getLiquidAssets() {
		return liquidAssets;
	}

	/**
	 * @param cl
	 * @param x
	 * @param y
	 * @return The price set by the network service provider for this specific network
	 * at this location.  This method should be overridden if NSPs to not set prices
	 * in individual network objects.
	 */
	@SuppressWarnings("unchecked")
	public Double getPrice(Class cl, AbstractConsumer cc, int x, int y) {
		for(AbstractNetwork n : (AbstractNetwork[]) this.networks.getObjectsAtLocation(x, y).objs) {
			if (cl.isInstance(n)) {
				return n.getPrice(cc);
			}
		}
		return null;
	}

	public Double getTotalCapitalExpenditures() {
		return totalCapitalExpenditures;
	}

	public Double getTotalInterestPaid() {
		return totalInterestPaid;
	}

	public Double getTotalRevenueCollected() {
		return totalRevenueCollected;
	}

	protected abstract void makeNetworkInvestment();
	
	private void serviceDebt() {
		Double interestPayment = debt * Exogenous.interestRate;
		Double principalPayment = debt * Exogenous.paybackRate;
		liquidAssets = liquidAssets - (interestPayment + principalPayment);
		debt = debt - principalPayment;
		totalInterestPaid += interestPayment;
	}
	
	protected abstract void setPrices();
	
	
	/**
	 * NOTE:  The order in which this function is called vis-a-vis other
	 * agents is unspecified.
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {
		billCustomers();
		serviceDebt();
		setPrices();
		makeNetworkInvestment();
	}

}
