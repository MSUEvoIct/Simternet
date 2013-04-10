package simternet.network;

import simternet.nsp.NSP;

/**
 * BackboneNetworks are those operated by Network Service Providers. They
 * connect to their own edge networks, other network providers, and some
 * application service providers.
 * 
 * @author kkoning
 * 
 */
public class Backbone extends Network {
	private static final long		serialVersionUID	= 1L;

	public final NSP	owner;

	public Backbone(final NSP nsp) {
		owner = nsp;
	}

}
