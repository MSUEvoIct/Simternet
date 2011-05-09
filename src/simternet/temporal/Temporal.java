package simternet.temporal;

import java.io.Serializable;

/**
 * The Temporal class was designed to hold a current and future state of any
 * class used by Simternet. In order to ensure the current state remains intact,
 * it creates deep copies when advancing time steps.
 * 
 * <p>
 * TODO: Update this description... :)
 * 
 * <p>
 * OPTIMIZE: Consider abandoning this approach and simply implementing our own
 * primitive wrapper types. This would have the advantage of explicitly
 * controlling modification of the scalar value in one place, rather than
 * depending on awkward code on the 'user' end of this class. For example, we
 * could provide a method to add a specified amount to the future value, rather
 * than requiring user code to retrieve the <i>future</i> value, as opposed to
 * the current one, and perform the addition itself. This would likely have the
 * advantage of being able to remove the getFuture() method completely.
 * 
 * @author samatkins
 * @author kkoning
 * @param <T>
 *            A supported class to Temporal-ize. Supported classes currently
 *            include:
 *            <ol>
 *            <li>Double</li> <li>Integer</li>
 *            </ol>
 */
public class Temporal<T> implements AsyncUpdate, Serializable {

	private static final long	serialVersionUID	= 1L;

	/**
	 * Simple testing method.
	 * 
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Temporal td = new Temporal<Double>(0.0);
		System.out.println(td.future);
		td.set(100.0);
		System.out.println(td.future);
		td.reduce(5.0);
		System.out.println(td.future);
	}

	private T	current		= null;

	private T	future		= null;
	private T	past		= null;
	private T	resetValue	= null;

	/**
	 * @param current
	 *            Set initial value, derive future value from copy.
	 */
	public Temporal(T current) {
		this.current = current;
		this.future = this.clone(this.current);
	}

	public Temporal(T current, T resetValue) {
		this(current);
		this.resetValue = resetValue;
	}

	@SuppressWarnings("unchecked")
	private T clone(T src) {
		T dst;
		if (src == null)
			dst = null;
		else if (src instanceof Double)
			dst = (T) CloneHelper.cloneDouble((Double) this.current);
		else if (src instanceof Integer)
			dst = (T) CloneHelper.cloneInteger((Integer) this.current);
		else
			throw new RuntimeException("Unable to clone " + this.current.getClass().getCanonicalName());
		return dst;
	}

	public T get() {
		return this.current;
	}

	/**
	 * @return the value at the next Step.
	 */
	public T getFuture() {
		return this.future;
	}

	public T getPast() {
		return this.past;
	}

	public T getPastDelta() {
		T toReturn;

		if (!this.isNumeric())
			throw new RuntimeException("Cannot calculate a change for a non-numeric type!");

		if (this.current instanceof Double) {
			Double past = (Double) this.past;
			if (past == null)
				past = 0.0;
			Double current = (Double) this.current;
			Double change = current - past;
			toReturn = (T) change;
		} else if (this.current instanceof Integer) {
			Integer past = (Integer) this.past;
			if (past == null)
				past = 0;
			Integer current = (Integer) this.current;
			Integer change = current - past;
			toReturn = (T) change;
		} else
			throw new RuntimeException("Delta of " + this.current.getClass() + " unimplemented");

		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public final void increase(Number amount) {
		if (!this.isNumeric())
			throw new RuntimeException("Cannot increment a non-numeric type!");

		if (this.future instanceof Double) {
			Double futureDouble = (Double) this.future;
			futureDouble += (Double) amount;
			this.future = (T) futureDouble;
		} else if (this.future instanceof Integer) {
			Integer futureInteger = (Integer) this.future;
			futureInteger += (Integer) amount;
			this.future = (T) futureInteger;
		} else
			throw new RuntimeException("Increment of type unimplemented");

	}

	public final boolean isNumeric() {
		if (this.future instanceof Number)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public final void reduce(T amount) {
		if (!this.isNumeric())
			throw new RuntimeException("Cannot increment a non-numeric type!");

		if (this.future instanceof Double) {
			Double futureDouble = (Double) this.future;
			futureDouble -= (Double) amount;
			this.future = (T) futureDouble;
		} else if (this.future instanceof Integer) {
			Integer futureInteger = (Integer) this.future;
			futureInteger -= (Integer) amount;
			this.future = (T) futureInteger;
		} else
			throw new RuntimeException("Increment of type unimplemented");

	}

	/**
	 * Required for use with immutable classes, particularly the primitives such
	 * as Double, Integer, etc... For mutable objects, use getFuture and operate
	 * directly on the object.
	 * 
	 * @param future
	 */
	public void set(T future) {
		this.future = future;
	}

	@Override
	public String toString() {
		return this.past + "->" + this.current + "->" + this.future + "(" + this.resetValue + ")";
	}

	public void update() {
		this.past = this.current;
		this.current = this.future;
		if (this.resetValue != null)
			this.future = this.clone(this.resetValue);
		else if (this.isNumeric())
			this.future = this.clone(this.current);
		else
			this.future = this.current;
	}

}
