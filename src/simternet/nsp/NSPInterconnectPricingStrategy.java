package simternet.nsp;

import simternet.application.ApplicationProvider;

public interface NSPInterconnectPricingStrategy {
	public Double getASPTransitPrice(ApplicationProvider other);
}
