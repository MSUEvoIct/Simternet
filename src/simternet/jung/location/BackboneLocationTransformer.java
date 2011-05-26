package simternet.jung.location;

import java.awt.Dimension;

import simternet.network.Backbone;
import simternet.network.Network;

public class BackboneLocationTransformer extends ColumnLocationTransformer {

	public BackboneLocationTransformer(Dimension d) {
		super(d);
	}

	@Override
	public boolean handles(Network vertex) {
		return (vertex instanceof Backbone);
	}
}
