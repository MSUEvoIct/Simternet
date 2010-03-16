package simternet;

/**
 * @author kkoning
 *
 * This NSP only offers one type of networ: SimpleNetwork, and it always
 * charges a uniform, unvarying price.
 *
 */
public class DumbNetworkServiceProvider extends AbstractNetworkProvider {
	
	/**
	 * DumbNetworkServiceProviders always offer service for exactly 15.0
	 */
	private static Double price = 15.0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean built = false;
	
	public DumbNetworkServiceProvider(Simternet s) {
		super(s);
	}
	
	/* (non-Javadoc)
	 * @see simternet.NetworkServiceProvider#makeNetworkInvestment()
	 * 
	 * Build a network at each and every location on the map.  Only
	 * do it once.
	 * 
	 */
	@Override
	protected void makeNetworkInvestment() {
		if (built == false)
			super.buildEverywhere(SimpleNetwork.class);
		built = true;
	}

	/* 
	 * Set the price in every square to the same static value.  Since we're
	 * building everywhere, don't bother only setting prices in locations at
	 * which we've built.
	 * 
	 */
	@Override
	protected void setPrices() {
		// Do nothing, price is always fixed.
	}

	@SuppressWarnings("unchecked")
	@Override
	public Double getPrice(Class cl, AbstractConsumer cc, int x, int y) {
		return price;
	}
	

}
