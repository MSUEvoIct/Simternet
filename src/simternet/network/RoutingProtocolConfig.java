package simternet.network;

public enum RoutingProtocolConfig {
	/**
	 * Send no routing updates
	 */
	NONE,
	/**
	 * Send routes only for locally connected networks
	 */
	PEER,
	/**
	 * Send all routes
	 */
	TRANSIT
}
