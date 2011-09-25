package simternet.gui.inspector;

import java.util.Collection;


import simternet.agents.asp.ApplicationProvider;
import simternet.engine.Simternet;
import simternet.gui.inspector.property.DoubleProperty;

/**
 * An inspector that does not represent a single agent, but rather all
 * Application Providers in a given simulation
 * 
 * @author graysonwright
 * 
 */
public class GlobalASPInspector extends Inspector {

	protected DoubleProperty	averageNumCustomers;

	DoubleProperty				maxPrice, averagePrice, minPrice;

	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes an inspector for all Application Providers in a given
	 * simulation
	 * 
	 * @param sim
	 *            the simulation from which to pull the Application Providers
	 */
	public GlobalASPInspector(Simternet sim) {
		this(sim, "Global ASP Inspector");
	}

	/**
	 * Initializes an inspector for all Application Providers in a given
	 * simulation
	 * 
	 * @param sim
	 *            the simulation from which to pull the Application Providers
	 * @param title
	 *            the title to place on the inspector's window
	 */
	public GlobalASPInspector(Simternet sim, String title) {
		super(sim, title);

		maxPrice = new DoubleProperty("Maximum Price", sim);
		this.add(maxPrice);

		minPrice = new DoubleProperty("Minimum Price", sim);
		this.add(minPrice);

		averagePrice = new DoubleProperty("Average Price", sim);
		this.add(averagePrice);

		averageNumCustomers = new DoubleProperty("Average Number of Customers", sim);
		this.add(averageNumCustomers);

		this.update();
	}

	@Override
	public void update() {

		Collection<ApplicationProvider> asps = sim.getASPs();

		if (asps.size() == 0) {
			maxPrice.setValue(0);
			averagePrice.setValue(0);
			minPrice.setValue(0);
			return;
		}

		double minPrice = Double.MAX_VALUE;
		double maxPrice = -Double.MAX_VALUE;
		double totalPrice = 0;
		double totalNumCustomers = 0;

		// sweep through list, calculating averages and totals
		for (ApplicationProvider asp : asps) {
			if (asp.getPriceSubscriptions() < minPrice) {
				minPrice = asp.getPriceSubscriptions();
			}
			if (asp.getPriceSubscriptions() > maxPrice) {
				maxPrice = asp.getPriceSubscriptions();
			}
			totalPrice += asp.getPriceSubscriptions();
			totalNumCustomers += asp.getCustomers();
		}

		this.maxPrice.setValue(maxPrice);
		this.minPrice.setValue(minPrice);
		averagePrice.setValue(totalPrice / asps.size());

		averageNumCustomers.setValue(totalNumCustomers / asps.size());
	}

}
