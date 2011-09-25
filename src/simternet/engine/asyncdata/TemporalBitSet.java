package simternet.engine.asyncdata;

import java.util.BitSet;

public class TemporalBitSet extends BitSet implements AsyncUpdate {

	private static final long serialVersionUID = 1L;

	boolean changed = false;

	BitSet future;

	@Override
	public void and(BitSet set) {
		this.changed = true;
		this.future.and(set);
	}

	@Override
	public void andNot(BitSet set) {
		this.changed = true;
		this.future.andNot(set);
	}

	@Override
	public void clear() {
		this.changed = true;
		this.future.clear();
	}

	@Override
	public void clear(int bitIndex) {
		this.changed = true;
		this.future.clear(bitIndex);
	}

	@Override
	public void clear(int fromIndex, int toIndex) {
		this.changed = true;
		this.future.clear(fromIndex, toIndex);
	}

	@Override
	public void flip(int bitIndex) {
		this.changed = true;
		this.future.flip(bitIndex);
	}

	@Override
	public void flip(int fromIndex, int toIndex) {
		this.changed = true;
		this.future.flip(fromIndex, toIndex);
	}

	@Override
	public void or(BitSet set) {
		this.changed = true;
		this.future.or(set);
	}

	@Override
	public void set(int bitIndex) {
		this.changed = true;
		this.future.set(bitIndex);
	}

	@Override
	public void set(int bitIndex, boolean value) {
		this.changed = true;
		this.future.set(bitIndex, value);
	}

	@Override
	public void set(int fromIndex, int toIndex) {
		this.changed = true;
		this.future.set(fromIndex, toIndex);
	}

	@Override
	public void set(int fromIndex, int toIndex, boolean value) {
		this.changed = true;
		this.future.set(fromIndex, toIndex, value);
	}

	@Override
	public void update() {
		if (this.changed) {
			this.clear();
			this.or(this.future);
			this.future = (BitSet) this.clone();
		}
	}

	@Override
	public void xor(BitSet set) {
		this.changed = true;
		this.future.xor(set);
	}

}
