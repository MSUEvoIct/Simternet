package simternet.network;

import java.io.Serializable;

public class RoutePreference implements Serializable {

	public static final Integer	ONE_BETTER			= -1;
	public static final Integer	SAME				= 0;
	private static final long	serialVersionUID	= 1L;
	public static final Integer	TWO_BETTER			= 1;

	/**
	 * Compares candidate backbone links. Perfers transit links, then links with
	 * higher bandwidth
	 * 
	 * @param one
	 * @param two
	 * @return ONE_BETTER, SAME, or TWO_BETTER
	 */
	public int compareDefaultRoute(BackboneLink one, BackboneLink two) {
		return two.bandwidth.compareTo(one.bandwidth);
	}

	/**
	 * @param one
	 *            The first Route
	 * @param two
	 *            The second Route
	 * @return ONE_BETTER, SAME, or TWO_BETTER
	 */
	public int compareRoutes(Route one, Route two) {

		// Sanity check, both routes should be to the same destination network
		if (!one.destination.equals(two.destination))
			throw new RuntimeException("Comparing Routes with different destinations");

		// if distances are equal, use the link with higher bandwidth
		if (one.distance == two.distance)
			return two.nextHop.bandwidth.compareTo(one.nextHop.bandwidth);

		// otherwise, prefer route with the shorter path
		return one.distance.compareTo(two.distance);
	}

}
