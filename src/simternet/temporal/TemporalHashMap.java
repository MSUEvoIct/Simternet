package simternet.temporal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemporalHashMap<K, V> extends HashMap<K, V> implements AsyncUpdate {

	private static final long serialVersionUID = 1L;

	private List<K> toRemove = null;
	private Map<K, V> updates = null;
	/**
	 * Assume V extends AsyncUpdate until we know otherwise.
	 */
	boolean updateValues = true;

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
		// update before removal
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
