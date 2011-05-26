package simternet.jung.location;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;

import simternet.jung.PriorityTransformer;
import simternet.network.Network;

/**
 * ColumnLocationTransformer
 * 
 * A transformer to move Backbones to their correct location onscreen.
 * 
 * See comments for <PriorityTransformer.java>
 * 
 * @author graysonwright
 */
public abstract class ColumnLocationTransformer extends PriorityTransformer<Network, Point2D> {

	private Dimension			dimension;
	HashMap<Network, Integer>	numberSystem;

	int							numNetworksPlaced;
	final static int			diameter	= 20;

	public ColumnLocationTransformer(Dimension d) {
		this.numNetworksPlaced = 0;
		this.dimension = d;
		this.numberSystem = new HashMap<Network, Integer>();
	}

	@Override
	public Point2D transform(Network net) {
		if (!this.handles(net))
			return null;

		int num;

		// if the network has already been placed, use its previously assigned
		// number
		if (this.numberSystem.containsKey(net))
			num = this.numberSystem.get(net).intValue();
		// otherwise, assign it a number and use that.
		else {
			num = this.numNetworksPlaced;
			this.numNetworksPlaced++;
			this.numberSystem.put(net, Integer.valueOf(num));
		}

		int numRows = this.dimension.height / ColumnLocationTransformer.diameter;
		int numCols = this.dimension.width / ColumnLocationTransformer.diameter;

		// leave every other row and column empty
		numRows /= 2;
		numCols /= 2;

		int col = num / numRows;
		int row = num % numRows;

		if (col >= numCols)
			System.err.println("Too many networks placed in the graph viewer. Will not be able to display them all.");

		int x = col * 40;
		int y = (row * 40) + ((col % 2) * 20);

		return new Point(x, y);

	}
}
