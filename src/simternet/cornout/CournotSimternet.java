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
			if ( ((CournotNetworkServiceProvider)nsp).getPreviousMarketShare().isNaN() == false ){
				sum += ((CournotNetworkServiceProvider)nsp).getPreviousMarketShare();
			}
		}
		if (sum == 0.0){
			return new Double(0.0);
		}
		if (sum - np.getPreviousMarketShare() == 0.0){
			return new Double(1.0);
		}
		
		Double marketShare = new Double((sum-np.getPreviousMarketShare())/sum);
		return marketShare;
	}
	
	public Double getCurrentMarketSharePercentage(CournotNetworkServiceProvider np){
		double sum = 0.0;
		ArrayList<AbstractNetworkProvider> nsps = (ArrayList<AbstractNetworkProvider>) getNetworkServiceProviders();
		for (AbstractNetworkProvider nsp : nsps){
			if ( ((CournotNetworkServiceProvider)nsp).getMarketShare().isNaN() == false ){
				sum += ((CournotNetworkServiceProvider)nsp).getMarketShare();
			}
		}
		if (sum == 0.0){
			return new Double(0.0);
		}
		if (sum - np.getMarketShare() == 0.0){
			return new Double(1.0);
		}
		
		Double marketShare = new Double((sum-np.getMarketShare())/sum);
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
