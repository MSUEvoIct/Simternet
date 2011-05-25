package simternet.jung;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;

import simternet.network.Backbone;
import simternet.network.Network;

/**
 * BackboneLocationTransformer
 * 
 * A transformer to move Backbones to their correct location onscreen.
 * 
 * See comments for <LocationTransformer.java>
 * 
 * @author graysonwright
 */
public class BackboneLocationTransformer extends LocationTransformer {

	private Dimension			dimension;
	int							numBackbonesPlaced;

	HashMap<Network, Integer>	numberSystem;
	final static int			diameter	= 20;

	public BackboneLocationTransformer(Dimension d) {
		this.numBackbonesPlaced = 0;
		this.dimension = d;
		this.numberSystem = new HashMap<Network, Integer>();
	}

	@Override
	public boolean handles(Network net) {
		return (net instanceof Backbone);
	}

	@Override
	public Point2D transform(Network net) {
		if (!this.handles(net))
			return null;

		int num;

		// if the backbone has already been placed, use its previously assigned
		// number
		if (this.numberSystem.containsKey(net))
			num = this.numberSystem.get(net).intValue();
		// otherwise, assign it a number and use that.
		else {
			num = this.numBackbonesPlaced;
			this.numBackbonesPlaced++;
			this.numberSystem.put(net, Integer.valueOf(num));
		}

		int numRows = this.dimension.height / BackboneLocationTransformer.diameter;
		int numCols = this.dimension.width / BackboneLocationTransformer.diameter;

		// leave every other row and column empty
		numRows /= 2;
		numCols /= 2;

		int col = num / numRows;
		int row = num % numRows;

		if (col >= numCols)
			System.err.println("Too many backbones placed in the graph viewer. Will not be able to display them all.");

		int x = col * 40;
		int y = (row * 40) + ((col % 2) * 20);

		return new Point(x, y);

	}
}
