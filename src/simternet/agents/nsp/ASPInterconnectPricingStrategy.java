package simternet.agents.nsp;

import simternet.agents.asp.ApplicationProvider;

public interface ASPInterconnectPricingStrategy {
	public Double getASPTransitPrice(ApplicationProvider other);
}
