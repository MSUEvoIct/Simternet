package simternet.network;

import simternet.Exogenous;
import simternet.consumer.AbstractConsumerClass;

public class SimpleNetwork extends AbstractNetwork {

	private Double price = null;
	
	@Override
	public Double getBuildCost() {
		Double cost = 0.0;
		Double population = nsp.getSimternet().getPopulation(this.locationX, this.locationY);
		
		cost += Exogenous.netCostSimpleArea;
		cost += population * Exogenous.netCostSimpleUser;
		
		return cost;
	}

	/* (non-Javadoc)
	 * @see simternet.Network#getPrice(simternet.ConsumerClass)
	 * 
	 * There is no price discrimination on a SimpleNetwork.
	 * 
	 */
	@Override
	public Double getPrice(AbstractConsumerClass cc) {
		return price;
	}

	@Override
	public void setPrice(AbstractConsumerClass cc, Double price) {
		this.price = price;
	}

//	public SimpleNetwork deepCopy(){
//		SimpleNetwork ret = new SimpleNetwork();
//		ret.setLocationX(locationX);
//		ret.setLocationY(locationY);
//		for (Entry<AbstractConsumerClass, Double> e: getCustomers().entrySet()){
//			ret.setCustomers(cc, numCustomers)
//		}
//		return ret;
//	}
	
}
