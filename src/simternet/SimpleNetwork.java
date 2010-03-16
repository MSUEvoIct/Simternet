package simternet;

public class SimpleNetwork extends AbstractNetwork {

	private Double price = null;
	
	@Override
	public Double getBuildCost() {
		Double cost = 0.0;
		Double population = nsp.s.getPopulation(this.locationX, this.locationY);
		
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
	public Double getPrice(AbstractConsumer cc) {
		return price;
	}

	@Override
	public void setPrice(AbstractConsumer cc, Double price) {
		this.price = price;
	}

}
