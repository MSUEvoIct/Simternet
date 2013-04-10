package simternet.network;

import java.io.Serializable;
import java.util.Vector;

public class Route implements Serializable, Cloneable {

	private static final long	serialVersionUID	= 1L;
	final Network				destination;
	Integer						distance;
	BackboneLink				nextHop;
	Vector<Network>				path;

	public Route(Network destination, BackboneLink nextHop, Integer distance) {
		this.destination = destination;
		this.nextHop = nextHop;
		this.distance = distance;
		this.path = new Vector<Network>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Route clone() throws CloneNotSupportedException {
		Route newRoute = (Route) super.clone();
		// Our network path will be separate from the clone source's path
		newRoute.path = (Vector<Network>) this.path.clone();
		return newRoute;
	}

	public Network getDestination() {
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
