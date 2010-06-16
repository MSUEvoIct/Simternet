package simternet.nsp;

import sim.util.Int2D;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;

public interface PricingStrategy {
	public Double getPrice(Class<? extends AbstractNetwork> cl,
			AbstractConsumerClass cc, Int2D location);

	public void setPrices();
}
