package simternet.engine.asyncdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * As with TemporalHashMap, changes are intercepted and deferred until update()
 * is called, at which time they are committed to the underlying SparseGrid2D.
 * 
 * <p>
 * If the values stored in this Map themselves implement the AsyncUpdate
 * interface, they will be updated when this Map's update function is called by
 * the Arbiter. This allows pseudo-recursive updates and makes deep copying of
 * these data structures (hopefully) unnecessary..
 * 
 * <p>
 * FIXME: Processing of multiple changes and removals is likely broken. See the
 * note at @see simternet.temporal.TemporalHashMap
 * 
 * <p>
 * OPTIMIZE: SparseGrid2D does not make use of Java generics, meaning there is
 * no run-time guarantee that the contained objects will be of a specified type.
 * This precludes significant run-time optimization for those SparseGrid2D
 * objects which do not contain primarily (or exclusively) objects that
 * implement AsyncUpdate because each object must be checked, at each update
 * cycle, to see if it implements AsyncUpdate or not. A simple but effective
 * optimization is possible if there is a guarantee that all objects either
 * implement AsyncUpdate or they do not.
 * 
 * @author kkoning
 * @see simternet.engine.asyncdata.TemporalHashMap
 * @see simternet.engine.asyncdata.Arbiter
 */
public class TemporalSparseGrid2D extends SparseGrid2D implements AsyncUpdate, Serializable, Iterable {

	private class LocatedObject implements Serializable {
		Int2D						location;
		Object						obj;

		private static final long	serialVersionUID	= 1L;

		private LocatedObject(Object obj, Int2D location) {
			this.obj = obj;
			this.location = location;
		}
	}

	private List<Object>		toRemove			= null;

	private List<Int2D>			toRemoveLoc			= null;
	private List<LocatedObject>	toSet				= null;
	private static final long	serialVersionUID	= 1L;

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
				AsyncUpdate au = (AsyncUpdate) obj;
				au.update();
			}
		}

	}

}
