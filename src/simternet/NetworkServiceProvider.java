package simternet;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;

public class NetworkServiceProvider implements Steppable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Double liquidAssets = 0.0;
	public Double debt = 0.0;

	public Double totalRevenueCollected = 0.0;
	public Double totalCapitalExpenditures = 0.0;
	public Double totalInterestPaid = 0.0;

	public IntGrid2D network;
	public DoubleGrid2D prices;
	public DoubleGrid2D marketShare;
	
	private Simternet simternet = null;

	public NetworkServiceProvider() {
		getEndowment();
		initNetwork();
		initPrices();
		initMarketShare();
	}

	/*
	 * (non-Javadoc)
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

	private void makeNetworkInvestment() {

	}

	private void setPrices() {

	}

	private void billCustomers() {
		for (int x = 0; x < Simternet.landscapeWidth; x++)
			for (int y = 0; y < Simternet.landscapeHeight; y++) {
				if (this.network.field[x][y] == 0) continue;  // If there is no network there can't be customers
				Double revenue = 
					(simternet.population.field[x][y] * this.marketShare.field[x][y])
					* this.prices.field[x][y];
				liquidAssets += revenue;
				totalRevenueCollected += revenue;
			}
	}
	
	/**
	 * The NSP has decided to build a network at x,y.  Build the network
	 * and increase the NSP's debt by the cost.
	 */
	private void buildNetwork(int x, int y) {
		Double cost = Simternet.netCostPhoneArea + 
			(Simternet.netCostPhoneUser * simternet.population.field[x][y]);
		debt += cost;
		totalCapitalExpenditures += cost;
		
		network.field[x][y] = 1;
	}

	private void serviceDebt() {
		Double interestPayment = debt * Simternet.interestRate;
		Double principalPayment = debt * Simternet.paybackRate;
		liquidAssets = liquidAssets - (interestPayment + principalPayment);
		debt = debt - principalPayment;
		totalInterestPaid += interestPayment;
	}

	/**
	 * Initially, this NSP has no network at any locations;
	 */
	private void initNetwork() {
		network = new IntGrid2D(Simternet.landscapeWidth,
				Simternet.landscapeHeight, 0);
	}

	/**
	 * Set initial prices so high (10,000) that no consumer will purchase
	 * service.  This is the equivalent of setting the default quantity
	 * provided to 0.
	 */
	private void initPrices() {
		prices = new DoubleGrid2D(Simternet.landscapeWidth,
				Simternet.landscapeHeight, 10000);
	}
	
	/**
	 * A network service provider does not start with any customers.
	 */
	private void initMarketShare() {
		marketShare = new DoubleGrid2D(Simternet.landscapeWidth,
				Simternet.landscapeHeight, 0.0);
	}
	
	private void getEndowment() {
		liquidAssets += Simternet.nspEndowment;
	}

}
