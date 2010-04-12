package simternet.temporal;

/**
 * 
 * When agents update data upon which other agents will take action,
 * syncronously updating these values at decision time potentially creates race
 * conditions. To avoid this problem, Simternet agents (and their data objects)
 * implement this interface to separate the decision process from the resulting
 * changes to data structures.
 * 
 * The updates are committed to the underlying data structures when the update
 * method is called by the Arbiter.
 * 
 * @see simternet.temporal.Arbiter
 * @author kkoning
 */
public interface AsyncUpdate {
	/**
	 * Commit the changes made to this object to the underlying data structure.
	 * For example, if the implementing object is a hash map, it should apply
	 * all the changes it has saved and deferred to the backing HashMap.
	 */
	public void update();
}
