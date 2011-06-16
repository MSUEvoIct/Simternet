package simternet.jung.appearance;

import org.apache.commons.collections15.Transformer;

import simternet.network.Backbone;
import simternet.network.Datacenter;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * Labels Network providers and Application Providers with the short form of
 * their name
 * 
 * @author graysonwright
 * 
 */
public class NetworkLabeller implements Transformer<Network, String> {

	@Override
	public String transform(Network net) {

		if (net instanceof EdgeNetwork)
			return null;
		else if (net instanceof Backbone)
			return ((Backbone) net).getOwner().getName();
		else if (net instanceof Datacenter)
			return ((Datacenter) net).getOwner().getName();

		return null;
	}

}
