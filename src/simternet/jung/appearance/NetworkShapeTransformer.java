package simternet.jung.appearance;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;

import simternet.Simternet;
import simternet.jung.ConsumerNetwork;
import simternet.network.Datacenter;
import simternet.network.Network;

/**
 * Defines the size and shape of Network objects in the graph.
 * 
 * @author graysonwright
 */
public class NetworkShapeTransformer implements Transformer<Network, Shape> {

	protected Simternet	s;
	protected double	scale	= .002;

	public NetworkShapeTransformer(Simternet s) {
		super();
		this.s = s;
	}

	public NetworkShapeTransformer(Simternet s, double scale) {
		super();
		this.s = s;
		if (scale > 0)
			this.scale = scale;
	}

	@Override
	public Shape transform(Network net) {
		if (net instanceof ConsumerNetwork) {

			Double pop = ((ConsumerNetwork) net).getPopulation();

			Shape shape = new Ellipse2D.Double(-pop * this.scale / 2, -pop * this.scale / 2, pop * this.scale, pop
					* this.scale);
			return shape;

		} else if (net instanceof Datacenter) {
			int x[] = { 0, 10, -10 };
			int y[] = { -10, 10, 10 };
			return new Polygon(x, y, 3);
		} else
			// if (net instanceof Backbone)
			return new Rectangle2D.Double(-10, -10, 20, 20);
	}
}
