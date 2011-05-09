package simternet;

import java.util.Iterator;

import sim.util.Int2D;

public class LocationIterator implements Iterator<Int2D>, Iterable<Int2D> {
	private int			remaining;
	private int			xCur	= 0;
	private final int	xMax;
	private int			yCur	= 0;
	private final int	yMax;

	public LocationIterator(int x, int y) {
		this.xMax = x - 1;
		this.yMax = y - 1;
		this.remaining = x * y;
	}

	public LocationIterator(Simternet s) {
		this(s.config.x(), s.config.y());
	}

	@Override
	public boolean hasNext() {
		if (this.remaining > 0)
			return true;
		return false;
	}

	@Override
	public Iterator<Int2D> iterator() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Int2D next() {
		Int2D toReturn = new Int2D(this.xCur, this.yCur);
		this.xCur++;
		if (this.xCur > this.xMax) {
			this.xCur = 0;
			this.yCur++;
		}
		this.remaining--;

		return toReturn;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Cannot remove a location.");

	}

}
