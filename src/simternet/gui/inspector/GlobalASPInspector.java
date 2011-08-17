package simternet.gui.inspector;

import java.util.Collection;

import simternet.application.ApplicationProvider;
import simternet.gui.GUI;
import simternet.gui.inspector.property.DoubleProperty;

public class GlobalASPInspector extends Inspector {

	protected DoubleProperty	averageNumCustomers;

	DoubleProperty				maxPrice, averagePrice, minPrice;

	private static final long	serialVersionUID	= 1L;

	public GlobalASPInspector(GUI gui) {
		this("Global ASP Inspector", gui);
	}

	public GlobalASPInspector(String title, GUI owner) {
		super(title, owner);

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
		} else {
			double minPrice = Double.MAX_VALUE;
			double maxPrice = -Double.MAX_VALUE;
			double totalPrice = 0;
			double totalNumCustomers = 0;

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
}
