package simternet.jung.inspector;

import java.util.Collection;

import simternet.application.ApplicationProvider;
import simternet.jung.gui.GUI;
import simternet.jung.inspector.property.DoubleProperty;

public class GlobalASPInspector extends Inspector {

	protected DoubleProperty	averageNumCustomers;

	DoubleProperty				maxPrice, averagePrice, minPrice;

	private static final long	serialVersionUID	= 1L;

	public GlobalASPInspector(GUI gui) {
		this("Global ASP Inspector", gui);
	}

	public GlobalASPInspector(String title, GUI owner) {
		super(title, owner);

		this.maxPrice = new DoubleProperty("Maximum Price");
		this.add(this.maxPrice);

		this.minPrice = new DoubleProperty("Minimum Price");
		this.add(this.minPrice);

		this.averagePrice = new DoubleProperty("Average Price");
		this.add(this.averagePrice);

		this.averageNumCustomers = new DoubleProperty("Average Number of Customers");
		this.add(this.averageNumCustomers);

		this.update();
	}

	@Override
	public void update() {

		Collection<ApplicationProvider> asps = GUI.getSimternet().getASPs();

		if (asps.size() == 0) {
			this.maxPrice.setValue(0);
			this.averagePrice.setValue(0);
			this.minPrice.setValue(0);
		} else {
			double minPrice = Double.MAX_VALUE;
			double maxPrice = -Double.MAX_VALUE;
			double totalPrice = 0;
			double totalNumCustomers = 0;

			for (ApplicationProvider asp : asps) {
				if (asp.getPriceSubscriptions() < minPrice)
					minPrice = asp.getPriceSubscriptions();
				if (asp.getPriceSubscriptions() > maxPrice)
					maxPrice = asp.getPriceSubscriptions();
				totalPrice += asp.getPriceSubscriptions();
				totalNumCustomers += asp.getCustomers();
			}

			this.maxPrice.setValue(maxPrice);
			this.minPrice.setValue(minPrice);
			this.averagePrice.setValue(totalPrice / asps.size());

			this.averageNumCustomers.setValue(totalNumCustomers / asps.size());
		}
	}
}
