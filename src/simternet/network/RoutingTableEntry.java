package simternet.network;

public class RoutingTableEntry {
	public final AbstractNetwork destination;
	public Integer distance;
	public BackboneLink nextHop;

	public RoutingTableEntry(AbstractNetwork destination, BackboneLink nextHop,
			Integer distance) {
		this.destination = destination;
		this.nextHop = nextHop;
		this.distance = distance;
	}

	public AbstractNetwork getDestination() {
		return this.destination;
	}

	public Integer getDistance() {
		return this.distance;
	}

	public BackboneLink getNextHop() {
		return this.nextHop;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public void setNextHop(BackboneLink nextHop) {
		this.nextHop = nextHop;
	}
}
