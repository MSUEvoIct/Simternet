package simternet.network;

public class Link {

	protected final double bandwidth;
	protected final AbstractNetwork destination;
	protected final double latency;

	public Link(final AbstractNetwork destination, final double bandwidth,
			final double latency) {
		this.destination = destination;
		this.bandwidth = bandwidth;
		this.latency = latency;
	}

}
