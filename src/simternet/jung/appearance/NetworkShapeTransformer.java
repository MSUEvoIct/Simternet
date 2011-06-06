package simternet.jung.appearance;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;

import simternet.Simternet;
import simternet.network.Datacenter;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

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
		if (net instanceof EdgeNetwork)
			return (new EdgeShapeTransformer(this.s).transform((EdgeNetwork) net));
		else if (net instanceof Datacenter) {
			int x[] = { 0, 10, -10 };
			int y[] = { -10, 10, 10 };
			return new Polygon(x, y, 3);
		} else
			// if (net instanceof Backbone)
			return new Rectangle2D.Double(-10, -10, 20, 20);
	}
}
