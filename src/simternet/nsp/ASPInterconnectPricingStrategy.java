package simternet.nsp;

import simternet.application.ApplicationProvider;

public interface ASPInterconnectPricingStrategy {
	public Double getASPTransitPrice(ApplicationProvider other);
}
