package simternet;


public class Temporal<T> {

	private T current;
	private T future;
	
	public T getCurrent() {
		return current;
	}
	public void setFuture(T future) {
		this.future = future;
	}
	
	public void update(T cloneForFuture) {
		current = future;
		future = cloneForFuture;
	}
	
	
	
}
