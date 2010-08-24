package simternet.network;

public class Link {

	public static Link infiniteLink(final AbstractNetwork source,
			final AbstractNetwork destination) {
		Link l = new Link(source, destination, Double.MAX_VALUE,
				Double.MIN_VALUE);
		return l;
	}

	public static Link symmetricLink(Link simplex) {
		Link duplex = new Link(simplex.destination, simplex.source,
				simplex.bandwidth, simplex.latency);
		return duplex;
	}

	protected double bandwidth;
	protected final AbstractNetwork destination;
	protected double latency;
	protected boolean qosCapable;
	protected final AbstractNetwork source;

	public Link(final AbstractNetwork source,
			final AbstractNetwork destination, final double bandwidth,
			final double latency) {
		this.source = source;
		this.destination = destination;
		this.bandwidth = bandwidth;
		this.latency = latency;
	}

}
