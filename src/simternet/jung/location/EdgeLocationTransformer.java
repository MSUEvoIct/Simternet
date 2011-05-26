package simternet.jung.location;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import sim.util.Int2D;
import simternet.jung.PriorityTransformer;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * A transformer to move Edge Networks to their correct location onscreen. They
 * are arranged in a grid pattern.
 * 
 * See comments for <PriorityTransformer.java>
 * 
 * @author graysonwright
 */
public class EdgeLocationTransformer extends PriorityTransformer<Network, Point2D> {

	Dimension	cellSize;

	public EdgeLocationTransformer(Dimension cSize) {
		this.cellSize = cSize;
	}

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

		return pixelPoint;
	}
}
