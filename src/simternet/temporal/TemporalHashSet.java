package simternet.temporal;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TemporalHashSet<E> extends HashSet<E> implements AsyncUpdate,
		Serializable {

	private static final long serialVersionUID = 1L;

	private boolean clear = false;
	private Set<E> toAdd = new HashSet<E>();
	private Set<Object> toRemove = new HashSet<Object>();
	/**
	 * We will only attempt to run update() method on value
	 */
	private boolean updateValues = true;

	@Override
	public boolean add(E e) {
		this.toAdd.add(e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		this.toAdd.addAll(c);
		return true;
	}

	@Override
	public void clear() {
		this.clear = true;
	}

	@Override
	public boolean remove(Object o) {
		this.toRemove.add(o);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		this.toRemove.addAll(c);
		return true;
	}

	@Override
	public void update() {
		this.addAll(this.toAdd);
		this.toAdd.clear();
		this.removeAll(this.toRemove);
		this.toRemove.clear();
		if (this.clear) {
			this.clear();
			this.clear = false;
		}

		// If the elements of this set are themselves implement AsyncUpdate,
		// cascade the updates.
		if (this.updateValues)
			for (E entry : this)
				if (entry instanceof AsyncUpdate) {
					AsyncUpdate au = (AsyncUpdate) entry;
					au.update();
				} else {
					this.updateValues = false;
					break;
				}

	}

}
