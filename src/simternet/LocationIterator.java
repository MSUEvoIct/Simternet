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
		xMax = x - 1;
		yMax = y - 1;
		remaining = x * y;
	}

	public LocationIterator(Simternet s) {
		this(s.config.gridSize.x, s.config.gridSize.y);
	}

	@Override
	public boolean hasNext() {
		if (remaining > 0)
			return true;
		return false;
	}

	@Override
	public Iterator<Int2D> iterator() {
		return this;
	}

	@Override
	public Int2D next() {
		Int2D toReturn = new Int2D(xCur, yCur);
		xCur++;
		if (xCur > xMax) {
			xCur = 0;
			yCur++;
		}
		remaining--;

		return toReturn;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Cannot remove a location.");

	}

}
