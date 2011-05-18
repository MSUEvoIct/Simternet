package simternet.jung;

/** 
 * LocationTransformer
 * 
 * An abstract class that defines transformers that are used to place Network objects in their correct onscreen positions.
 * Subclasses will override methods <handles> and <transform> to deal with specific types of Networks.
 * 
 * @author graysonwright
 */

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import simternet.network.Network;

public abstract class LocationTransformer implements Transformer<Network, Point2D>, Comparable<LocationTransformer> {

	protected int	priority;

	public int compareTo(LocationTransformer transformer) {
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
	public abstract boolean handles(Network net);

	public void setPriority(int p) {
		this.priority = p;
	}

	@Override
	public abstract Point2D transform(Network net);
}