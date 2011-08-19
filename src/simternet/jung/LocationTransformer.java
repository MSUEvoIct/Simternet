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
 * A transformer that will lay out the contents of the graph onscreen. Handles
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
	 *            a backbone network to place
	 * @return the point in the JUNG display that net should be placed
	 */
	protected Point2D transform(Backbone net) {
		int x = 0, y = 0;

		// leave some space for the ConsumerNetworks...
		x += gap.width;
		x += sim.config.gridSize.x * cellDimension.width;
		x += gap.width;

		y += gap.height;
		y += (net.getOwner().getNumber() - 1) * cellDimension.height;

		return new Point(x, y);
	}

	/**
	 * Place a ConsumerNetwork object. These get placed in a grid on the left
	 * side of the visualization.
	 * 
	 * @param net
	 *            a consumer network to place
	 * @return the point in the JUNG display that net should be placed
	 */
	protected Point2D transform(ConsumerNetwork net) {
		int x = 0, y = 0;

		x += gap.width;
		x += net.getLocation().x * cellDimension.width;

		y += gap.height;
		y += net.getLocation().y * cellDimension.height;

		return new Point(x, y);
	}

	/**
	 * Place a Datacenter object. These get placed to the far right, past the
	 * ConsumerNetworks and Backbones.
	 * 
	 * @param net
	 *            a datacenter network to place
	 * @return the point in the JUNG display that net should be placed
	 */
	protected Point2D transform(Datacenter net) {
		int x = 0, y = 0;

		// Leave some space for the ConsumerNetworks...
		x += gap.width;
		x += sim.config.gridSize.y * cellDimension.width;
		x += gap.width;
		// And for the Backbones...
		x += cellDimension.width;
		x += gap.width;

		y += gap.height;
		y += (net.getOwner().getNumber() - 1) * cellDimension.height;

		return new Point(x, y);
	}

	/**
	 * Checks the type of the network to transform, and sends it to the
	 * appropriate helper method
	 */
	@Override
	public Point2D transform(Network net) {
		if (net instanceof ConsumerNetwork)
			return this.transform((ConsumerNetwork) net);
		if (net instanceof Backbone)
			return this.transform((Backbone) net);
		if (net instanceof Datacenter)
			return this.transform((Datacenter) net);
		// If we see a point at (-50,-50) -- top left -- we know it's an error.
		System.err.println("Trying to place an object in the visualization that shouldn't be there.");
		return new Point(-50, -50);
	}

}
