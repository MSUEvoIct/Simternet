package simternet.jung;

import java.awt.Dimension;
import java.util.PriorityQueue;

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
public abstract class CompositeTransformer<V, E> extends PriorityTransformer<V, E> {

	private PriorityQueue<PriorityTransformer<V, E>>	transformerQueue;

	public CompositeTransformer() {
		this.transformerQueue = new PriorityQueue<PriorityTransformer<V, E>>();
	}

	/**
	 * Stores a transformer, which it will later ask to transform objects (if it
	 * can handle them)
	 * 
	 * @param transformer
	 *            the transformer to add
	 */
	public void addTransformer(PriorityTransformer<V, E> transformer, int priority, Dimension offset) {
		this.transformerQueue.add(transformer);
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
		for (PriorityTransformer<V, E> ntpTransformer : this.transformerQueue)
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
	public E transform(V vertex) {
		// TODO Auto-generated method stub

		if (!this.handles(vertex))
			return null;

		E result = null;

		for (PriorityTransformer<V, E> ntpTransformer : this.transformerQueue)
			if (ntpTransformer.handles(vertex))
				result = ntpTransformer.transform(vertex);

		return result;
	}

}
