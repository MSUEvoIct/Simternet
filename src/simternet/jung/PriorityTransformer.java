package simternet.jung;

/** 
 * PriorityTransformer
 * 
 * An abstract class that defines transformers that are used to place Network objects in their correct onscreen positions.
 * Subclasses will override methods <handles> and <transform> to deal with specific types of Networks.
 * 
 * @author graysonwright
 */

import org.apache.commons.collections15.Transformer;

public abstract class PriorityTransformer<V, E> implements Transformer<V, E>, Comparable<PriorityTransformer<V, E>> {

	protected int	priority	= 1;

	public int compareTo(PriorityTransformer<V, E> transformer) {
		return transformer.getPriority() - this.getPriority();
	}

	public int getPriority() {
		return this.priority;
	}

	/*
	 * Search subclasses in the queue (should be sorted from lowest # priority
	 * to highest) if any subclass can handle the network, this class can as
	 * well
	 */
	public abstract boolean handles(V vertex);

	public void setPriority(int p) {
		this.priority = p;
	}
}