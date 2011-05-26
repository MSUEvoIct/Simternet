package simternet.jung.location;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

import simternet.jung.PriorityTransformer;

/**
 * RandomLocationTransformer
 * 
 * A transformer to move any Network to a random location onscreen.
 * 
 * See comments for <PriorityTransformer.java>
 * 
 * @author graysonwright
 */
public class RandomLocationTransformer<V> extends PriorityTransformer<V, Point2D> {

	private Dimension	dimension;

	public RandomLocationTransformer(Dimension d) {
		this.dimension = d;
	}

	@Override
	public boolean handles(V vertex) {
		return true;
	}

	@Override
	public Point2D transform(V vertex) {

		Random random = new Random();
		Point2D randomPoint = new Point();
		randomPoint.setLocation(random.nextFloat() * this.dimension.width, random.nextFloat() * this.dimension.height);
		return randomPoint;
	}
}
