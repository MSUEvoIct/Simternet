package simternet.network;

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
		double buildCost;
		double numUsersAtLocation = builder.s.getPopulation(location);

		if (type.equals(SimpleEdgeNetwork.class)) {
			buildCost = builder.s.config.networkSimpleBuildCostFixed + builder.s.config.networkSimpleBuildCostPerUser
					* numUsersAtLocation;
		} else
			throw new RuntimeException("Don't have build costs for edge networks other than SimpleEdgeNetwork");

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
		assetFinance = new AssetFinance(this, this.owner.financials);

		Double firstPrice = this.owner.pricingStrategy.getEdgePrice(this);
		price = new Temporal<Double>(firstPrice);
	}

	public boolean isCongested() {
		if (getUpstreamIngress().congestionAlgorithm.getUsageRatio() >= 1)
			return true;
		else
			return false;
	}

	public Int2D getLocation() {
		return location;
	}

	public Double getMaxBandwidth() {
		return maxBandwidth.get();
	}

	@SuppressWarnings("unchecked")
	public Double getNumSubscribers() {
		double customers = 0;

		Bag b = owner.s.getConsumerClasses().getObjectsAtLocation(getLocation());
		if (b == null)
			return 0.0;

		Iterator<Consumer> i = b.iterator();

		while (i.hasNext()) {
			Consumer acc = i.next();
			customers += acc.getSubscribers(this);
		}
		return customers;
	}

	/**
	 * @return the cost of operating this network per step.
	 */
	public abstract Double getOperationCost();

	public NetworkProvider getOwner() {
		return owner;
	}

	public double getUsageRatio() {
		double usageRatio = getUpstreamIngress().congestionAlgorithm.getUsageRatio();
		return usageRatio;
	}

	public Double getPrice() {
		return price.get();
	}

	public Double getPriceFuture() {
		return price.getFuture();
	}

	public BackboneLink getUpstreamIngress() {
		int i = 0;
		BackboneLink l = null;
		for (BackboneLink link : ingressLinks.values()) {
			i++;
			l = link;
		}
		if (i == 1)
			return l;

		Logger.getLogger("Network").log(Level.ERROR, "Num of Ingress links for " + this + " = " + i);
		return null;
	}

	public void processUsage(Consumer users) {
		receivePayment(users);
	}

	public void receivePayment(Consumer acc) {
		double price = owner.pricingStrategy.getEdgePrice(this);
		double revenue = acc.getPopulation() * price;
		owner.financials.earn(revenue);

		if (TraceConfig.consumerPaidNSP && Logger.getRootLogger().isTraceEnabled()) {
			Logger.getRootLogger().trace(acc + " paid " + price + " for " + this);
		}
	}

	public void sendFlowsToCustomers() {

		/*
		 * We need to iterate over every flow, received on every ingress link.
		 * (Actually, there should be only one, since this is an edge network,
		 * but other networks can have many ingress links.
		 */
		for (BackboneLink link : ingressLinks.values()) {
			List<NetFlow> flows = link.receiveFlows();
			for (NetFlow flow : flows) {
				/*
				 * Process congestion information first. This includes 1)
				 * further congesting to the maximum bandwidth of this edge
				 * network, 2) informing the sending network of the congestion,
				 * 3) noting the congestion ourselves.
				 */
				flow.congest(getMaxBandwidth());
				if (flow.isCongested()) {
					flow.source.noteCongestion(flow);
					noteCongestion(flow);
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
		sendFlowsToCustomers();
		// Pay for operating this network.
		double operationsCost = getOperationCost();
		owner.financials.earn(-operationsCost);
	}

	@Override
	public String toString() {
		return "Edge of " + owner.toString() + " @" + getLocation();
	}

	@Override
	public void update() {
		super.update();
		maxBandwidth.update();
		price.update();
	}
}
