package simternet.jung.location;

import java.awt.Dimension;

import simternet.network.Datacenter;
import simternet.network.Network;

/**
 * A transformer to move DataCenters to their correct location onscreen.
 * 
 * See comments for <ColumnLocationTransformer.java>
 * 
 * @author graysonwright
 */
public class DatacenterLocationTransformer extends ColumnLocationTransformer {

	public DatacenterLocationTransformer(Dimension d) {
		super(d);
	}

	@Override
	public boolean handles(Network vertex) {
		return (vertex instanceof Datacenter);
	}

}
