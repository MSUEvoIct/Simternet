package sandbox;

import java.util.ArrayList;

import javax.activation.UnsupportedDataTypeException;

import simternet.CournotSimternet;
import simternet.Exogenous;
import simternet.Simternet;
import simternet.Temporal;
import simternet.consumer.CournotConsumer;
import simternet.network.AbstractNetwork;
import simternet.network.Network2D;
import simternet.network.SimpleNetwork;
import simternet.nsp.CournotNetworkServiceProvider;

public class main {

	public static Network2D networkGrid = null;
	public static CournotNetworkServiceProvider nsp = null;
	
	/**
	 * @param args
	 * @throws UnsupportedDataTypeException 
	 */
	public static void main(String[] args) throws UnsupportedDataTypeException {
		CournotSimternet s = new CournotSimternet(0);
		networkGrid = new Network2D(100,100);
		nsp = new CournotNetworkServiceProvider(s);
		buildEverywhere(SimpleNetwork.class);
		Network2D copy = networkGrid.deepCopy();
		buildEverywhere(SimpleNetwork.class);
	}
	
	/**
	 * @param network
	 * 
	 * Utility method:  build the specified network everywhere.
	 * 
	 */
	protected static void buildEverywhere(Class<? extends AbstractNetwork> network) {
		for (int x = 0; x < Exogenous.landscapeX; x++)
			for (int y = 0; y < Exogenous.landscapeY; y++)
				buildNetwork(network, x, y);
	}

	/**
	 * The NSP has decided to build a network at x,y.  Build the network
	 * and increase the NSP's debt by the cost.
	 * 
	 * Future: verify provider is not building network twice?
	 */
	private static void buildNetwork(Class<? extends AbstractNetwork> cl, Integer x, Integer y) {
		try {
			AbstractNetwork network = (AbstractNetwork) cl.newInstance(); // Create a new network, but of the specified type.
			network.init(nsp, x, y); // give this network information about its position and owner.
			Double buildCost = network.getBuildCost(); 
			networkGrid.setObjectLocation(network, x, y); // Add the network to our collection.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
