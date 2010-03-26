package simternet.consumer;

import java.util.Collections;
import java.util.Map;

import sim.engine.SimState;
import simternet.*;
import simternet.network.SimpleNetwork;
import simternet.nsp.AbstractNetworkProvider;
import simternet.nsp.CournotNetworkServiceProvider;

public class CournotConsumer extends SimpleConsumer{

	public CournotConsumer(Simternet s) {
		super(s);
		// TODO Auto-generated constructor stub
	}
	
	public void step(SimState state){
		//Sanity check:
		//Make sure all network providers have set their prices
		for(AbstractNetworkProvider np : s.getNetworkServiceProviders()){
			CournotNetworkServiceProvider nsp = (CournotNetworkServiceProvider)np;
			if(nsp.isPriceSet().booleanValue() == false){
				System.out.println("Consumer can not be run before all NSPs have run");
				System.exit(0);
			}
		}
		
		makePurchaseDecisions();
		for(AbstractNetworkProvider n: s.getNetworkServiceProviders()) {
			CournotNetworkServiceProvider nsp = (CournotNetworkServiceProvider)n;
			System.out.println("Market Share: " + (nsp.getTotalSubscribers() / s.getPopulation()) + "\nPrice: " + nsp.getPrice());
		}
		System.out.println("----------------");
		
		//reset network service providers
		for(AbstractNetworkProvider np : s.getNetworkServiceProviders()){
			CournotNetworkServiceProvider nsp = (CournotNetworkServiceProvider)np;
			nsp.advanceOneStep();
		}
	}
	
	protected void makePurchaseDecisions() {
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				makePurchaseDecisionsAt(x, y);
	}
	
	protected void makePurchaseDecisionsAt(Integer x, Integer y) {
		makePurchaseDecisionsForSimpleNetworkAt(x,y);
	}
	
	private void makePurchaseDecisionsForSimpleNetworkAt(Integer x, Integer y) {
		
		for(AbstractNetworkProvider n: s.getNetworkServiceProviders()) {
			CournotNetworkServiceProvider nsp = (CournotNetworkServiceProvider)n;
			// Previous code didn't run, might wanna check that
			if(nsp.hasNetworkAt(SimpleNetwork.class, x, y)){
				int numberOfProviders = s.getNetworkServiceProviders().size();
				Double qtyDemanded = new Double( (CournotSimternet.ALPHA - nsp.getPrice()) / numberOfProviders );
				//Assumption!
				qtyDemanded = new Double( (qtyDemanded / CournotSimternet.ALPHA) * s.getPopulation(x, y));
				//End assumption
				//qtyDemanded = new Double(.25 * s.getPopulation(x, y));
				nsp.setCustomers(SimpleNetwork.class, this, x, y, qtyDemanded);
				nsp.setTotalSubscribers(nsp.getTotalSubscribers() + qtyDemanded);
			}
		}
		
	}
	
}