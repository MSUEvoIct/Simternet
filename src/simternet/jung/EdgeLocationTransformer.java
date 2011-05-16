package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import sim.util.Int2D;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

public class EdgeLocationTransformer extends LocationTransformer {

	Dimension	cellSize;

	// Dimension maximum;

	public EdgeLocationTransformer(Dimension cSize) {
		// this.dimension = d;
		this.cellSize = cSize;
		// this.maximum = new Dimension(0, 0);
	}

	// public Dimension getMaximum() {
	// return this.maximum;
	// }

	@Override
	public boolean handles(Network net) {
		if (net instanceof EdgeNetwork)
			return true;
		else
			return false;
	}

	@Override
	public Point2D transform(Network net) {

		if (!this.handles(net))
			return null;

		Int2D loc = ((EdgeNetwork) net).getLocation();
		Point pixelPoint = new Point(loc.x, loc.y);

		pixelPoint.x = pixelPoint.x * this.cellSize.width;
		pixelPoint.y = pixelPoint.y * this.cellSize.height;

		// if (pixelPoint.x > this.maximum.width)
		// this.maximum.setSize(pixelPoint.x, this.maximum.height);
		// if (pixelPoint.y > this.maximum.height)
		// this.maximum.setSize(this.maximum.width, pixelPoint.y);

		return pixelPoint;
	}
}
