package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

import simternet.network.Datacenter;
import simternet.network.Network;

public class DatacenterLocationTransformer extends LocationTransformer {

	private Dimension	dimension;
	private Random		random;

	public DatacenterLocationTransformer(Dimension d) {
		this.dimension = d;
		this.random = new Random();
	}

	@Override
	public boolean handles(Network net) {
		return (net instanceof Datacenter);
	}

	@Override
	public Point2D transform(Network net) {
		// TODO Create a better-than-random distribution
		if (!this.handles(net))
			return null;
		return new Point(this.dimension.width / 2, (int) (this.random.nextFloat() * this.dimension.height));

	}

}
