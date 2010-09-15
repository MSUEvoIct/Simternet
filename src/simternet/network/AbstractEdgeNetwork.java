package simternet.network;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Int2D;
import simternet.consumer.AbstractConsumerClass;
import simternet.nsp.AbstractNetworkProvider;

public abstract class AbstractEdgeNetwork extends AbstractNetwork {

	private static final long serialVersionUID = 1L;

	/**
	 * Utility function used to determine the cost of building a network. AFAIK,
	 * this can only be done by either hard-coding the edge network classes
	 * (which is undesirable) or using reflection, because Java does not allow
	 * static methods to be declared in interfaces.
	 * 
	 * @param type
	 *            The class specifying the type of edge network
	 * @param builder
	 *            The service provider that would build it
	 * @param location
	 *            The location at which the service provider would build the
	 *            network
	 * @return The cost of building the network in question.
	 */
	public static Double getBuildCost(
			Class<? extends AbstractEdgeNetwork> type,
			AbstractNetworkProvider builder, Int2D location) {
		Double buildCost;
		try {
			Method m = type.getMethod("getBuildCost",
					AbstractNetworkProvider.class, Int2D.class);

			buildCost = (Double) m.invoke(null, builder, location);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return buildCost;
	}

	protected final Int2D location;

	protected final AbstractNetworkProvider owner;

	public AbstractEdgeNetwork(AbstractNetworkProvider owner, Int2D location) {
		this.owner = owner;
		this.location = location;
	}

	@SuppressWarnings("unchecked")
	public Double getNumSubscribers() {
		double customers = 0;

		Bag b = this.owner.simternet.getConsumerClasses().getObjectsAtLocation(
				this.location);
		if (b == null)
			return 0.0;

		Iterator<AbstractConsumerClass> i = b.iterator();

		while (i.hasNext()) {
			AbstractConsumerClass acc = i.next();
			customers += acc.getSubscribers(this);
		}
		return customers;
	}

	public void receivePayment(AbstractConsumerClass acc, Double numCustomers) {

		double price = this.owner.getPrice(this.getClass(), acc, this.location);
		double revenue = numCustomers * price;

		this.owner.financials.earn(revenue);
	}

	public void sendFlowsToCustomers() {
		// For now, just dump information on the flows...
		for (BackboneLink link : this.ingressLinks) {
			// should be only one link...
			List<NetFlow> flows = link.receiveFlows();
			for (NetFlow flow : flows)
				System.out.println(flow);
		}
	}

	@Override
	public void step(SimState state) {
		this.sendFlowsToCustomers();
	}

	@Override
	public void update() {
		super.update();

	}

}
