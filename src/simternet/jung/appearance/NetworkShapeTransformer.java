package simternet.jung.appearance;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;

import simternet.jung.ConsumerNetwork;
import simternet.network.Datacenter;
import simternet.network.Network;

/**
 * Defines the size and shape of Network objects in the graph.
 * 
 * @author graysonwright
 */
public class NetworkShapeTransformer implements Transformer<Network, Shape> {

	protected double	scale;

	public NetworkShapeTransformer() {
		this(.002);
	}

	public NetworkShapeTransformer(double scale) {
		super();
		if (scale > 0) {
			this.scale = scale;
		}
	}

	@Override
	public Shape transform(Network net) {
		if (net instanceof ConsumerNetwork) {
			// ConsumerNetworks are circles, with size dependent on their
			// population
			Double pop = ((ConsumerNetwork) net).getPopulation();
			Shape shape = new Ellipse2D.Double(-pop * scale / 2, -pop * scale / 2, pop * scale, pop * scale);
			return shape;

		} else if (net instanceof Datacenter) {
			// DataCenters are triangles
			int x[] = { 0, 10, -10 };
			int y[] = { -10, 10, 10 };
			return new Polygon(x, y, 3);

		} else
			// everything else is a rectangle
			// if (net instanceof Backbone)
			return new Rectangle2D.Double(-10, -10, 20, 20);
	}
}
