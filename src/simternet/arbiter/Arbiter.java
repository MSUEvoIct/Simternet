package simternet.arbiter;

import javax.activation.UnsupportedDataTypeException;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Simternet;
import simternet.nsp.AbstractNetworkProvider;

public class Arbiter implements Steppable{
	
	@Override
	public void step(SimState state) {
		for(AbstractNetworkProvider nsp: ((Simternet)state).getNetworkServiceProviders()){
			try {
				nsp.updateData(state);
			} catch (UnsupportedDataTypeException e) {
				//Shouldn't have caught this in the first place, but step() can't throw anything unless
				//I modify the source of Mason :\
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

}
