package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import simternet.Simternet;
import simternet.network.Backbone;
import simternet.network.Datacenter;
import simternet.network.Network;

/**
 * a transformer that will lay out the contents of the graph onscreen. Handles
 * ConsumerNetworks, Backbones, and Datacenters.
 * 
 * @author graysonwright
 */
public class LocationTransformer implements Transformer<Network, Point2D> {

	protected Dimension	cellDimension	= new Dimension(50, 50);
	protected Dimension	gap				= new Dimension(100, 100);

	protected Simternet	sim;

	public LocationTransformer(Simternet sim) {
		this.sim = sim;
	}

	/**
	 * Place a Backbone object. These get placed to the right of the
	 * ConsumerNetwork objects.
	 * 
	 * @param net
	 * @return
	 */
	protected Point2D transform(Backbone net) {
		int x = 0, y = 0;

		// leave some space for the ConsumerNetworks...
		x += this.gap.width;
		x += this.sim.config.x() * this.cellDimension.width;
		x += this.gap.width;

		y += this.gap.height;
		y += (net.getOwner().getNumber() - 1) * this.cellDimension.height;

		return (new Point(x, y));
	}

	/**
	 * Place a ConsumerNetwork object. These get placed in a grid on the left
	 * side of the visualization.
	 * 
	 * @param net
	 * @return
	 */
	protected Point2D transform(ConsumerNetwork net) {
		int x = 0, y = 0;

		x += this.gap.width;
		x += net.getLocation().x * this.cellDimension.width;

		y += this.gap.height;
		y += net.getLocation().y * this.cellDimension.height;

		return (new Point(x, y));
	}

	/**
	 * Place a Datacenter object. These get placed to the far right, past the
	 * ConsumerNetworks and Backbones.
	 * 
	 * @param net
	 * @return
	 */
	protected Point2D transform(Datacenter net) {
		int x = 0, y = 0;

		// Leave some space for the ConsumerNetworks...
		x += this.gap.width;
		x += this.sim.config.x() * this.cellDimension.width;
		x += this.gap.width;
		// And for the Backbones...
		x += this.cellDimension.width;
		x += this.gap.width;

		y += this.gap.height;
		y += (net.getOwner().getNumber() - 1) * this.cellDimension.height;

		return (new Point(x, y));
	}

	@Override
	public Point2D transform(Network net) {
		if (net instanceof ConsumerNetwork)
			return this.transform((ConsumerNetwork) net);
		if (net instanceof Backbone)
			return this.transform((Backbone) net);
		if (net instanceof Datacenter)
			return this.transform((Datacenter) net);
		// If we see a point at (-50,-50) we know it's an error.
		System.err.println("Trying to place an object in the visualization that shouldn't be there.");
		return new Point(-50, -50);
	}

}
