package simternet.jung.location;

import java.awt.Dimension;

import simternet.network.Backbone;
import simternet.network.Network;

/**
 * A transformer that defines the location of a BackboneLink in a JUNG graph.
 * See ColumnLocationTransformer
 * 
 * @author graysonwright
 */
public class BackboneLocationTransformer extends ColumnLocationTransformer {

	public BackboneLocationTransformer(Dimension d) {
		super(d);
	}

	@Override
	public boolean handles(Network vertex) {
		return (vertex instanceof Backbone);
	}
}
