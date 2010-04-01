package simternet;

import java.util.ArrayList;
import java.util.Set;

import simternet.*;
import simternet.arbiter.Arbiter;
import simternet.consumer.CournotConsumer;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.CournotNetworkServiceProvider;

public class CournotSimternet extends Simternet{
	
	public final static Double ALPHA = 100.0;
	
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
	
	public Double getCombinedCompetitorsMarketShare(CournotNetworkServiceProvider np){
		double sum = 0.0;
		Set<AbstractNetworkProvider> nsps = getNetworkServiceProviders();
		for (AbstractNetworkProvider nsp : nsps){
			if ( ((CournotNetworkServiceProvider)nsp).getPreviousTotalSubscribers().isNaN() == false ){
				sum += ((CournotNetworkServiceProvider)nsp).getPreviousTotalSubscribers();
			}
		}
		
		if(sum == 0)
			return new Double(0.0);
		if(sum - np.getPreviousTotalSubscribers() == 0)
			return new Double(0.0);
		Double marketShare = new Double((sum-np.getPreviousTotalSubscribers())/getPopulation());
		return marketShare;
	}
	
	public void start() {
        // reset schedule
        schedule.reset();
		initNetworkServiceProviders();
		initConsumerClasses();
		schedule.scheduleRepeating(new Arbiter(), 99999999, 1);
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
