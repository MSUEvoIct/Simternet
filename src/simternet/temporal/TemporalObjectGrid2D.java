package simternet.temporal;

import java.util.ArrayList;
import java.util.List;

import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;
import sim.util.IntBag;

/**
 * Temporal version of Mason's ObjectGrid2D. Could not extend the class directly
 * because its set() method was marked as final. Used encapsulation instead.
 * 
 * @author kkoning
 * 
 */
public class TemporalObjectGrid2D implements AsyncUpdate {

	private class Update {
		Object value;
		int x;
		int y;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The underlying ObjectGrid2D
	 */
	private ObjectGrid2D og;

	private List<Update> updates = new ArrayList<Update>();

	public TemporalObjectGrid2D(int width, int height) {
		this.og = new ObjectGrid2D(width, height);
	}

	public TemporalObjectGrid2D(int width, int height, Object initialValue) {
		this.og = new ObjectGrid2D(width, height, initialValue);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param values
	 */
	public TemporalObjectGrid2D(ObjectGrid2D values) {
		this.og = new ObjectGrid2D(values);
	}

	public Bag clear() {
		return this.og.clear();
	}

	public Object get(int x, int y) {
		return this.og.get(x, y);
	}

	public Bag getNeighborsHamiltonianDistance(int x, int y, int dist,
			boolean toroidal, Bag result, IntBag xPos, IntBag yPos) {
		return this.og.getNeighborsHamiltonianDistance(x, y, dist, toroidal,
				result, xPos, yPos);
	}

	public Bag getNeighborsHexagonalDistance(int x, int y, int dist,
			boolean toroidal, Bag result, IntBag xPos, IntBag yPos) {
		return this.og.getNeighborsHexagonalDistance(x, y, dist, toroidal,
				result, xPos, yPos);
	}

	public Bag getNeighborsMaxDistance(int x, int y, int dist,
			boolean toroidal, Bag result, IntBag xPos, IntBag yPos) {
		return this.og.getNeighborsMaxDistance(x, y, dist, toroidal, result,
				xPos, yPos);
	}

	void set(int x, int y, Object val) {
		Update ud = new Update();
		ud.x = x;
		ud.y = y;
		ud.value = val;
		this.updates.add(ud);
	}

	@Override
	public void update() {
		for (Update update : this.updates)
			this.og.set(update.x, update.y, update.value);
		this.updates.clear();
	}

}
