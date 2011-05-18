package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.PriorityQueue;

import simternet.network.Network;

/**
 * CompositeLocationTransformer
 * 
 * A composite transformer class. Holds a list of LocationTransformers that each
 * handle different types of Network. When asked to place a Network, this class
 * has one of the transformers in its queue handle the placement instead.
 * Priorities are used to decide which of these transformers to ask first.
 * 
 * @author graysonwright
 */
public class CompositeLocationTransformer extends LocationTransformer {
	private HashMap<LocationTransformer, Dimension>	offsetMap;
	private PriorityQueue<LocationTransformer>		transformerQueue;

	public CompositeLocationTransformer() {
		this.transformerQueue = new PriorityQueue<LocationTransformer>();
		this.offsetMap = new HashMap<LocationTransformer, Dimension>();
	}

	public void addTransformer(LocationTransformer transformer, int priority, Dimension offset) {
		transformer.setPriority(priority);
		this.transformerQueue.add(transformer);
		this.offsetMap.put(transformer, offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simternet.jung.LocationTransformer#handles(simternet.network.Network)
	 * 
	 * If any transformer in the queue can handle the Network, then this class
	 * can as well.
	 */
	@Override
	public boolean handles(Network net) {
		for (LocationTransformer ntpTransformer : this.transformerQueue)
			if (ntpTransformer.handles(net))
				return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simternet.jung.LocationTransformer#transform(simternet.network.Network)
	 * 
	 * Ask the lowest-priority transformer that handles this type of Network to
	 * place it.
	 */
	@Override
	public Point2D transform(Network net) {
		// TODO Auto-generated method stub

		if (!this.handles(net))
			return null;

		Point result = null;

		for (LocationTransformer ntpTransformer : this.transformerQueue)
			if (ntpTransformer.handles(net)) {
				result = (Point) ntpTransformer.transform(net);
				Dimension offset = this.offsetMap.get(ntpTransformer);
				result.x += offset.width;
				result.y += offset.height;
			}

		return result;
	}

}
