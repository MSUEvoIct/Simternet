package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

import simternet.network.Network;

/**
 * RandomLocationTransformer
 * 
 * A transformer to move any Network to a random location onscreen.
 * 
 * See comments for <LocationTransformer.java>
 * 
 * @author graysonwright
 */
public class RandomLocationTransformer extends LocationTransformer {

	private Dimension	dimension;

	public RandomLocationTransformer(Dimension d) {
		this.dimension = d;
	}

	@Override
	public boolean handles(Network net) {
		return true;
	}

	@Override
	public Point2D transform(Network net) {

		Random random = new Random();
		Point2D randomPoint = new Point();
		randomPoint.setLocation(random.nextFloat() * this.dimension.width, random.nextFloat() * this.dimension.height);
		return randomPoint;
	}
}
