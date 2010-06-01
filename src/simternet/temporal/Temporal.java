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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Simple testing method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Temporal td = new Temporal<Double>(0.0);
		System.out.println(td.future);
		td.set(100.0);
		System.out.println(td.future);
		td.decrement(5.0);
		System.out.println(td.future);
	}

	private T current = null;
	private T future = null;

	private T resetValue = null;

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
		if (src instanceof Double)
			dst = (T) CloneHelper.cloneDouble((Double) this.current);
		else if (src instanceof Integer)
			dst = (T) CloneHelper.cloneInteger((Integer) this.current);
		else
			throw new RuntimeException("Unable to clone "
					+ this.current.getClass().getCanonicalName());
		return dst;
	}

	@SuppressWarnings("unchecked")
	public final void decrement(T amount) {
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

	public T get() {
		return this.current;
	}

	/**
	 * @return the value at the next Step.
	 */
	public T getFuture() {
		return this.future;
	}

	@SuppressWarnings("unchecked")
	public final void increment(Number amount) {
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

	public void update() {
		this.current = this.future;
		if (this.resetValue != null)
			this.future = this.clone(this.resetValue);
		else
			this.future = this.clone(this.current);
	}

}
