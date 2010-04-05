package simternet.temporal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * @author kkoning
 * 
 *         Extends SparseGrid2D to only actually implement changes at update().
 *         Inserted objects must implement AsyncUpdate.
 */
public class TemporalSparseGrid2D extends SparseGrid2D implements AsyncUpdate {
	private class LocatedObject {
		Int2D location;
		Object obj;

		private LocatedObject(Object obj, Int2D location) {
			this.obj = obj;
			this.location = location;
		}
	}

	private static final long serialVersionUID = 7260152817905845782L;

	private List<Object> toRemove = null;
	private List<Int2D> toRemoveLoc = null;
	private List<LocatedObject> toSet = null;

	public TemporalSparseGrid2D(int width, int height) {
		super(width, height);
	}

	@Override
	public Object remove(Object obj) {
		if (this.toRemove == null)
			this.toRemove = new ArrayList<Object>();
		this.toRemove.add(obj);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.field.grid.SparseGrid2D#removeObjectsAtLocation(int, int)
	 * 
	 * Object removal delayed until update()
	 */
	@Override
	public Bag removeObjectsAtLocation(int x, int y) {
		if (this.toRemoveLoc == null)
			this.toRemoveLoc = new ArrayList<Int2D>();
		this.toRemoveLoc.add(new Int2D(x, y));
		return null;
	}

	@Override
	public boolean setObjectLocation(Object obj, int x, int y) {
		return this.setObjectLocation(obj, new Int2D(x, y));
	}

	@Override
	public boolean setObjectLocation(Object obj, Int2D location) {
		// TODO Check to make sure obj implements AsyncUpdate
		if (this.toSet == null)
			this.toSet = new ArrayList<LocatedObject>();
		this.toSet.add(new LocatedObject(obj, location));
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		// delayed implementation of remove(obj)
		if (this.toRemove != null) {
			for (Object obj : this.toRemove)
				super.remove(obj);
			this.toRemove = null;
		}

		// delayed implementation of remove(x,y)
		if (this.toRemoveLoc != null) {
			for (Int2D loc : this.toRemoveLoc)
				super.removeObjectsAtLocation(loc.x, loc.y);
			this.toRemoveLoc = null;
		}

		// delayed implementation of set(obj,loc)
		if (this.toSet != null) {
			for (LocatedObject lo : this.toSet)
				super.setObjectLocation(lo.obj, lo.location);
			this.toSet = null;
		}

		// Update all the objects we contain
		Iterator<Object> i = this.iterator();
		while (i.hasNext()) {
			Object obj = i.next();
			if (obj instanceof AsyncUpdate) {
				AsyncUpdate au = (AsyncUpdate) i.next();
				au.update();
			}
		}

	}

}
