package simternet.temporal;

import java.io.Serializable;
import java.util.Iterator;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Simternet;
import simternet.application.ApplicationServiceProvider;
import simternet.consumer.AbstractConsumerClass;
import simternet.nsp.AbstractNetworkProvider;

/**
 * 
 * The Arbiter is an agent responsible for committing all of the changes to the
 * model data at the end of each time step. To do this, it must ultimately cause
 * all model objects implementing the AsyncUpdate interface to commit the
 * changes they have saved to the ultimate data structures representing the
 * model.
 * 
 * The reason for such an implementation is the prevention of race conditions in
 * which the order in which agents are executed affects the output of the model.
 * 
 * Currently, the only top-level objects that implement the AsyncUpdate
 * interface are Network Service Providers, which will all descend from
 * AbstractNetworkProvider. As the model code progresses, this will start to
 * include other objects as well.
 * 
 * @author kkoning
 * 
 */
public class Arbiter implements Steppable, Serializable {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void step(SimState state) {
		for (AbstractNetworkProvider nsp : ((Simternet) state)
				.getNetworkServiceProviders())
			nsp.update();

		Iterator<AbstractConsumerClass> i = ((Simternet) state)
				.getConsumerClasses().iterator();
		while (i.hasNext()) {
			AbstractConsumerClass acc = i.next();
			acc.update();
		}

		for (ApplicationServiceProvider asp : ((Simternet) state)
				.getASPs())
			asp.update();

	}

}
