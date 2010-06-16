package simternet.temporal;

import sim.util.Int2D;

public class TemporalBitMap2D extends TemporalBitSet {

	private static final long serialVersionUID = 1L;
	protected int xSize;
	protected int ySize;

	public TemporalBitMap2D(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public void clear(Int2D location) {
		super.clear(this.indexOf(location));
	}

	public void flip(Int2D location) {
		super.flip(this.indexOf(location));
	}

	public boolean get(Int2D location) {
		return super.get(this.indexOf(location));
	}

	private final int indexOf(int x, int y) {
		int index = 0;
		index += y * this.xSize;
		index += x;

		return index;
	}

	private final int indexOf(Int2D location) {
		return this.indexOf(location.x, location.y);
	}

	public void set(Int2D location) {
		super.set(this.indexOf(location));
	}

	public void set(Int2D location, boolean value) {
		super.set(this.indexOf(location), value);
	}

	@Override
	public int size() {
		return this.xSize * this.ySize;
	}

}
