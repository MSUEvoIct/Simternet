package simternet.cornout;

import java.util.ArrayList;

import simternet.main.*;

public class CournotSimternet extends Simternet{

	public final static Double ALPHA = 15.0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1377483617523007211L;

	public CournotSimternet(long seed) {
		super(seed);
	}
	
	public static void main(String[] args) {
		doLoop(CournotSimternet.class, args);
        System.exit(0);
	}
	
	public Double getPreviousMarketSharePercentage(CournotNetworkServiceProvider np){
		double sum = 0.0;
		ArrayList<AbstractNetworkProvider> nsps = (ArrayList<AbstractNetworkProvider>) getNetworkServiceProviders();
		for (AbstractNetworkProvider nsp : nsps){
			if ( ((CournotNetworkServiceProvider)nsp).getPreviousTotalSubscribers().isNaN() == false ){
				sum += ((CournotNetworkServiceProvider)nsp).getPreviousTotalSubscribers();
			}
		}
		
		if(sum == 0)
			return new Double(0.0);
		if(sum - np.getTotalSubscribers() == 0)
			return new Double(0.0);
		
		Double marketShare = new Double((sum-np.getPreviousTotalSubscribers()) / getPopulation());
		return marketShare;
	}
	
	public Double getCurrentMarketSharePercentage(CournotNetworkServiceProvider np){
		double sum = 0.0;
		ArrayList<AbstractNetworkProvider> nsps = (ArrayList<AbstractNetworkProvider>) getNetworkServiceProviders();
		for (AbstractNetworkProvider nsp : nsps){
			if ( ((CournotNetworkServiceProvider)nsp).getTotalSubscribers().isNaN() == false ){
				sum += ((CournotNetworkServiceProvider)nsp).getTotalSubscribers();
			}
		}
		
		if(sum == 0)
			return new Double(0.0);
		if(sum - np.getTotalSubscribers() == 0)
			return new Double(0.0);
		Double marketShare = new Double((sum-np.getTotalSubscribers())/getPopulation());
		return marketShare;
	}
	
	public void start() {
        // reset schedule
        schedule.reset();
        
		initConsumerClasses();
		initNetworkServiceProviders();
	}
	
	protected void initConsumerClasses() {
		addConsumerClass(new CournotConsumer(this));
	}
	
	private void initNetworkServiceProviders() {
		addNetworkServiceProvider(new CournotNetworkServiceProvider(this));
		addNetworkServiceProvider(new CournotNetworkServiceProvider(this));
		addNetworkServiceProvider(new CournotNetworkServiceProvider(this));
	}
	
}
