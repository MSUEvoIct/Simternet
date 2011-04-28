package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.PriorityQueue;

import simternet.network.Network;

public class CompositeLayoutTransformer extends LayoutTransformer {
	private HashMap<LayoutTransformer, Dimension>	offsetMap;
	private PriorityQueue<LayoutTransformer>		transformerQueue;

	public CompositeLayoutTransformer() {
		this.transformerQueue = new PriorityQueue<LayoutTransformer>();
		this.offsetMap = new HashMap<LayoutTransformer, Dimension>();
	}

	public void addTransformer(LayoutTransformer transformer, int priority, Dimension offset) {
		transformer.setPriority(priority);
		this.transformerQueue.add(transformer);
		this.offsetMap.put(transformer, offset);
	}

	/*
	 * Search subclasses in the queue (should be sorted from lowest # priority
	 * to highest) if any transformer in the queue can handle the network, this
	 * class can handle it.
	 */
	@Override
	public boolean handles(Network net) {
		for (LayoutTransformer ntpTransformer : this.transformerQueue)
			if (ntpTransformer.handles(net))
				return true;
		return false;
	}

	@Override
	public Point2D transform(Network net) {
		// TODO Auto-generated method stub

		if (!this.handles(net))
			return null;

		Point result = null;

		for (LayoutTransformer ntpTransformer : this.transformerQueue)
			if (ntpTransformer.handles(net)) {
				result = (Point) ntpTransformer.transform(net);
				Dimension offset = this.offsetMap.get(ntpTransformer);
				result.x += offset.width;
				result.y += offset.height;
			}

		return result;
	}

}
