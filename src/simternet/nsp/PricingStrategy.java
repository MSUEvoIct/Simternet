package simternet.nsp;

import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;

public interface PricingStrategy {
	public void setPrices();
	public Double getPrice(Class<? extends AbstractNetwork> cl,
			AbstractConsumerClass cc, int x, int y);
}
