package simternet.jung.appearance;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.apache.commons.collections15.Transformer;

import simternet.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class EdgeShapeTransformer implements Transformer<Network, Shape> {

	protected Simternet	s;
	protected double	scale	= .002;

	public EdgeShapeTransformer(Simternet s) {
		super();
		this.s = s;
	}

	public EdgeShapeTransformer(Simternet s, double scale) {
		super();
		this.s = s;
		if (scale > 0)
			this.scale = scale;
	}

	@Override
	public Shape transform(Network net) {
		if (net instanceof EdgeNetwork) {
			EdgeNetwork edge = (EdgeNetwork) net;
			Double pop = this.s.getPopulation(edge.getLocation());
			Shape shape = new Ellipse2D.Double(-pop * this.scale / 2, -pop * this.scale / 2, pop * this.scale, pop
					* this.scale);

			return shape;
		} else
			return new Ellipse2D.Double(-10, -10, 20, 20);
	}
}
