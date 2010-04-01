package simternet;

import javax.activation.UnsupportedDataTypeException;

import simternet.network.Network2D;


/**
 * The Temporal class was designed to hold a current and future state of any class used by Simternet.
 * In order to ensure the current state remains intact, it creates deep copies when advancing time steps.
 * @author samatkins
 * @param <T> A supported class to Temporal-ize. Supported classes currently include: 
 * <ol>
 * 	<li>Double</li>
 * </ol>
 */
public class Temporal<T> {

	private T oldOne;
	private T newOne;
	
	public Temporal(T current, T future) {
		this.oldOne = current;
		this.newOne = future;
	}
	
	public T getOld() {
		return oldOne;
	}
	
	public T getNew() {
		return newOne;
	}
	
	public void setNew(T future) {
		this.newOne = future;
	}
	
	/**
	 * @throws UnsupportedDataTypeException
	 */
	@SuppressWarnings("unchecked")
	public void update() throws UnsupportedDataTypeException {
		Object clone;
		if(newOne instanceof Double){
			clone = new Double(0.0);
		}else if(newOne instanceof Network2D){
			Network2D net = (Network2D) newOne;
			clone = net.deepCopy();
		}else{
			throw new UnsupportedDataTypeException("Support for '" + newOne.getClass() + "' has not been added to Generic class 'Temporal'");
		}
		oldOne = newOne;
		newOne = (T)clone;
	}
	
}
