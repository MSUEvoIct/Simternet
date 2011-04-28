package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

import simternet.network.Backbone;
import simternet.network.Network;

public class BackboneLayoutTransformer extends LayoutTransformer {

	private Dimension	dimension;
	private Random		random;

	public BackboneLayoutTransformer(Dimension d) {
		// TODO Auto-generated constructor stub
		this.dimension = d;
		this.random = new Random();
	}

	@Override
	public boolean handles(Network net) {
		return (net instanceof Backbone);
	}

	@Override
	public Point2D transform(Network net) {
		// TODO Create a better-than-random distribution
		if (!this.handles(net))
			return null;
		return new Point(this.dimension.width / 2, (int) (this.random.nextFloat() * this.dimension.height));

	}
}
