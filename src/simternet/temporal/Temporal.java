package simternet.temporal;

import javax.activation.UnsupportedDataTypeException;


/**
 * The Temporal class was designed to hold a current and future state of any
 * class used by Simternet. In order to ensure the current state remains intact,
 * it creates deep copies when advancing time steps.
 * 
 * @author samatkins
 * @param <T>
 *            A supported class to Temporal-ize. Supported classes currently
 *            include:
 *            <ol>
 *            <li>Double</li>
 *            </ol>
 */
public class Temporal<T> implements AsyncUpdate {

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

	public T get() {
		return this.current;
	}

	/**
	 * @return
	 */
	public T getFuture() {
		return this.future;
	}

	/**
	 * @param future
	 * 
	 *            Required for use with immutable classes, particularly the
	 *            primitives such as Double, Integer, etc... For mutable
	 *            objects, call the
	 * 
	 */
	public void set(T future) {
		this.future = future;
	}

	/**
	 * @throws UnsupportedDataTypeException
	 */
	public void update() {
		this.current = this.future;
		if (this.resetValue != null)
			this.future = this.clone(this.resetValue);
		else
			this.future = this.clone(this.current);

		// Object clone;
		// if(future instanceof Double){
		// clone = new Double(0.0);
		// }else if(future instanceof Network2D){
		// Network2D net = (Network2D) future;
		// clone = net.deepCopy();
		// }else{
		// throw new RuntimeException("Support for '" + future.getClass() +
		// "' has not been added to Generic class 'Temporal'");
		// }
		// current = future;
		// future = (T)clone;
	}

}
