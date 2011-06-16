package simternet.jung.location;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.PriorityQueue;

import simternet.jung.CompositeTransformer;
import simternet.jung.PriorityTransformer;

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
public class CompositeLocationTransformer<V> extends CompositeTransformer<V, Point2D> {

	private HashMap<PriorityTransformer<V, Point2D>, Dimension>	offsetMap;
	private PriorityQueue<PriorityTransformer<V, Point2D>>		transformerQueue;

	public CompositeLocationTransformer() {
		this.transformerQueue = new PriorityQueue<PriorityTransformer<V, Point2D>>();
		this.offsetMap = new HashMap<PriorityTransformer<V, Point2D>, Dimension>();
	}

	@Override
	public void addTransformer(PriorityTransformer<V, Point2D> transformer, int priority, Dimension offset) {
		transformer.setPriority(priority);
		this.transformerQueue.add(transformer);
		this.offsetMap.put(transformer, offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simternet.jung.PriorityTransformer#handles(simternet.network.Network)
	 * 
	 * If any transformer in the queue can handle the Network, then this class
	 * can as well.
	 */
	@Override
	public boolean handles(V vertex) {
		for (PriorityTransformer<V, Point2D> ntpTransformer : this.transformerQueue)
			if (ntpTransformer.handles(vertex))
				return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simternet.jung.PriorityTransformer#transform(simternet.network.Network)
	 * 
	 * Ask the lowest-priority transformer that handles this type of Network to
	 * place it.
	 */
	@Override
	public Point2D transform(V vertex) {
		// TODO Auto-generated method stub

		if (!this.handles(vertex))
			return null;

		Point result = null;

		/*
		 * TODO: This does not traverse the priority queue in order. At this
		 * point, this does not make a big difference, because the Transformers
		 * we are using are mutually exclusive, and we don't need to worry about
		 * their priorities over one another.
		 */
		for (PriorityTransformer<V, Point2D> ntpTransformer : this.transformerQueue)
			if (ntpTransformer.handles(vertex)) {
				result = (Point) ntpTransformer.transform(vertex);
				Dimension offset = this.offsetMap.get(ntpTransformer);
				result.x += offset.width;
				result.y += offset.height;
			}

		return result;
	}

}
