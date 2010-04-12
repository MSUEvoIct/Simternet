package simternet.temporal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TemporalHashMaps defer changes made to a map until after the object's update
 * method is called. It does so by intercepting changes normally made by calling
 * the methods overridden below, which apply the requested changes to a set of
 * temporary data structures.
 * 
 * <p>
 * If the values stored in this Map themselves implement the AsyncUpdate
 * interface, they will be updated when this Map's update function is called by
 * the Arbiter. This allows pseudo-recursive updates and makes deep copying of
 * these data structures (hopefully) unnecessary.
 * 
 * <p>
 * FIXME: The processing of multiple updates and removals is broken. Consider
 * the case in which a key/value pair is first removed and then re-added to the
 * map. With the current implementation, the earlier removal will overwrite the
 * subsequent insertion.
 * 
 * <p>
 * OPTIMIZE? Re-factor this class into TemporalMap, allowing reuse of this code
 * with arbitrary underlying implementations of Map. This is likely to be
 * appropriate in instances where the Map has very few entries, which is likely
 * to be common in this model. A more lightweight map implementation may
 * alleviate problems with memory usage down the road.
 * 
 * <p>
 * This will require making TemporalMap an abstract class, rather than extending
 * any particular implementation directly (i.e., HashMap, as this implementation
 * does...) The underlying data object will therefore be a separate instance
 * variable rather than 'this', and all Map interface methods which are not
 * deferred will need to be redirected to the underlying map implementation.
 * E.g., Map.get().
 * 
 * @author kkoning
 * @see simternet.temporal.Arbiter
 * 
 * @param <K>
 *            The type used for the key
 * @param <V>
 *            The type used for the value. I considered making it extend
 *            AsyncUpdate, but that would preclude using this implementation for
 *            immutable wrapper types such as Double. A method using runtime
 *            detection of the value class was used instead, with a negligible
 *            performance penalty for non- AsyncUpdate types.
 */
public class TemporalHashMap<K, V> extends HashMap<K, V> implements AsyncUpdate {

	private static final long serialVersionUID = 1L;

	private List<K> toRemove = null;
	private Map<K, V> updates = null;

	/**
	 * We will only attempt to run update() method on value
	 */
	private boolean updateValues = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		if (this.updates == null)
			this.updates = new HashMap<K, V>();
		this.updates.put(key, value);
		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet())
			this.put(key, m.get(key));
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		if (this.toRemove == null)
			this.toRemove = new ArrayList<K>();

		this.toRemove.add((K) key);
		return this.get(key);
	}

	@Override
	public void update() {
		// Since updates are processed with the put() function,
		// they must be performed before removals or the removal
		// call will be ineffective.
		if (this.updates != null) {
			for (K key : this.updates.keySet())
				super.put(key, this.updates.get(key));
			this.updates = null;
		}

		// process removals
		if (this.toRemove != null)
			for (K key : this.toRemove)
				super.remove(key);

		if (this.updateValues)
			for (K key : this.keySet()) {
				V val = this.get(key);
				if (val instanceof AsyncUpdate) {
					AsyncUpdate au = (AsyncUpdate) val;
					au.update();
				} else {
					this.updateValues = false;
					break;
				}
			}

	}
}
