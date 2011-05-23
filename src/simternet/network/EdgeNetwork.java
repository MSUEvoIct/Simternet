package simternet.network;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Int2D;
import simternet.AssetFinance;
import simternet.TraceConfig;
import simternet.consumer.Consumer;
import simternet.nsp.NetworkProvider;
import simternet.temporal.Temporal;

public abstract class EdgeNetwork extends Network {

	private static final long	serialVersionUID	= 1L;

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
	public static Double getBuildCost(Class<? extends EdgeNetwork> type, NetworkProvider builder, Int2D location) {
		Double buildCost;
		try {
			Method m = type.getMethod("getBuildCost", NetworkProvider.class, Int2D.class);
			buildCost = (Double) m.invoke(null, builder, location);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return buildCost;
	}

	final AssetFinance		assetFinance;

	/**
	 * The location of this network in the landscape.
	 */
	final Int2D				location;

	/**
	 * The maximum bandwidth each edge connection can support. Unlike other
	 * networks, this is an instantaneous measure rather than a total transfer
	 * capacity per period. I.e., bytes per second, not bytes per month.
	 */
	Temporal<Double>		maxBandwidth	= new Temporal<Double>(0.0);

	/**
	 * The NSP that owns and operates this network.
	 */
	final NetworkProvider	owner;

	/**
	 * The price of this network
	 */
	Temporal<Double>		price			= new Temporal<Double>(0.0);

	public EdgeNetwork(NetworkProvider owner, Int2D location) {
		this.owner = owner;
		this.location = location;
		this.assetFinance = new AssetFinance(this, this.owner.financials);

		Double firstPrice = this.owner.pricingStrategy.getEdgePrice(this);
		this.price = new Temporal<Double>(firstPrice);
	}

	public String getCongestionReport() {
		BackboneLink bl = this.getUpstreamIngress();
		if (bl == null)
			return "Not Connected";

		return bl.congestionAlgorithm.getCongestionReport();
	}

	public Int2D getLocation() {
		return this.location;
	}

	public Double getMaxBandwidth() {
		return this.maxBandwidth.get();
	}

	@SuppressWarnings("unchecked")
	public Double getNumSubscribers() {
		double customers = 0;

		Bag b = this.owner.simternet.getConsumerClasses().getObjectsAtLocation(this.getLocation());
		if (b == null)
			return 0.0;

		Iterator<Consumer> i = b.iterator();

		while (i.hasNext()) {
			Consumer acc = i.next();
			customers += acc.getSubscribers(this);
		}
		return customers;
	}

	public Double getPrice() {
		return this.price.get();
	}

	public Double getPriceFuture() {
		return this.price.getFuture();
	}

	protected BackboneLink getUpstreamIngress() {
		int i = 0;
		BackboneLink l = null;
		for (BackboneLink link : this.ingressLinks.values()) {
			i++;
			l = link;
		}
		if (i == 1)
			return l;

		Logger.getLogger("Network").log(Level.ERROR, "Num of Ingress links for " + this + " = " + i);
		return null;

	}

	public void processUsage(Consumer users) {
		this.receivePayment(users);
	}

	public void receivePayment(Consumer acc) {
		double price = this.owner.pricingStrategy.getEdgePrice(this);
		double revenue = acc.getPopultation() * price;
		this.owner.financials.earn(revenue);

		if (TraceConfig.consumerPaidNSP && Logger.getRootLogger().isTraceEnabled())
			Logger.getRootLogger().trace(acc + " paid " + price + " for " + this);
	}

	public void sendFlowsToCustomers() {

		/*
		 * We need to iterate over every flow, received on every ingress link.
		 * (Actually, there should be only one, since this is an edge network,
		 * but other networks can have many ingress links.
		 */
		for (BackboneLink link : this.ingressLinks.values()) {
			List<NetFlow> flows = link.receiveFlows();
			for (NetFlow flow : flows) {
				/*
				 * Process congestion information first. This includes 1)
				 * further congesting to the maximum bandwidth of this edge
				 * network, 2) informing the sending network of the congestion,
				 * 3) noting the congestion ourselves.
				 */
				flow.congest(this.getMaxBandwidth());
				if (flow.isCongested()) {
					flow.source.noteCongestion(flow);
					this.noteCongestion(flow);
				}

				/*
				 * Flows are now ready to be received by the users. What users
				 * do with them from here is up to them. They may discard the
				 * information, track statistics, take certain actions, etc...
				 */
				flow.user.receiveFlow(flow);

			}
		}
	}

	public void setMaxBandwidth(Double maxBandwidth) {
		this.maxBandwidth.set(maxBandwidth);
	}

	public void setPrice(Double price) {
		this.price.set(price);
	}

	@Override
	public void step(SimState state) {
		this.sendFlowsToCustomers();
	}

	@Override
	public String toString() {
		return "Edge of " + this.owner.getName() + " @" + this.getLocation();
	}

	@Override
	public void update() {
		super.update();
		this.maxBandwidth.update();
		this.price.update();
	}
}
