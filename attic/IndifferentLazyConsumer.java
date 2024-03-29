//package simternet.consumer;
//
//import java.io.Serializable;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import simternet.Simternet;
//import simternet.network.AbstractNetwork;
//import simternet.network.SimpleNetwork;
//import simternet.nsp.AbstractNetworkProvider;
//
//public class IndifferentLazyConsumer extends AbstractConsumerClass implements
//		Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	public IndifferentLazyConsumer(Simternet s) {
//		super(s);
//	}
//
//	@Override
//	protected Double demand(Class<? extends AbstractNetwork> an, Double price,
//			Integer x, Integer y) {
//
//		if (an.equals(SimpleNetwork.class))
//			return this.demandSimpleNetwork(price, x, y);
//		return 0.0;
//	}
//
//	private Double demandSimpleNetwork(Double price, Integer x, Integer y) {
//		Double totPopAt = this.getPopulation(x, y);
//		Double q = (1 - (price / Double.parseDouble(this.s.parameters
//				.getProperty("comsumers.simple.maxPrice"))))
//				* totPopAt;
//		return q;
//	}
//
//	@Override
//	protected void makeConsumptionDecisionAt(Integer x, Integer y) {
//		Map<AbstractNetworkProvider, Double> prices = this.s.getPriceList(
//				SimpleNetwork.class, this, x, y);
//		if (prices == null) // no nsp has offered this service here
//			return;
//		if (prices.isEmpty()) // no nsp has offered this service here
//			return;
//
//		Map<AbstractNetworkProvider, Double> oldQtys = new HashMap<AbstractNetworkProvider, Double>();
//		Map<AbstractNetworkProvider, Double> equilibriumQty = new HashMap<AbstractNetworkProvider, Double>();
//
//		for (AbstractNetworkProvider anp : this.s.getNetworkServiceProviders()) {
//			oldQtys.put(anp, new Double(anp.getCustomers(SimpleNetwork.class,
//					this, x, y)));
//			equilibriumQty.put(anp, 0.0);
//		}
//
//		Double lowPrice = Collections.min(prices.values());
//		Double totalQtyDemanded = this.demandSimpleNetwork(lowPrice, x, y);
//
//		Set<AbstractNetworkProvider> indifferent = new HashSet<AbstractNetworkProvider>();
//
//		for (Map.Entry<AbstractNetworkProvider, Double> me : prices.entrySet())
//			if (me.getValue() - lowPrice < Double.parseDouble(this.s.parameters
//					.getProperty("consumers.simple.closeEnoughPrice")))
//				indifferent.add(me.getKey());
//
//		for (AbstractNetworkProvider nsp : indifferent)
//			equilibriumQty.put(nsp, totalQtyDemanded / indifferent.size());
//
//		for (AbstractNetworkProvider nsp : this.s.getNetworkServiceProviders()) {
//			Double actualQty = Double.parseDouble(this.s.parameters
//					.getProperty("proportionChange"))
//					* equilibriumQty.get(nsp)
//					+ (1 - Double.parseDouble(this.s.parameters
//							.getProperty("proportionChange")))
//					* oldQtys.get(nsp);
//			this.setNumSubscriptions(SimpleNetwork.class, nsp, x, y, actualQty);
//		}
//
//	}
//
// }
