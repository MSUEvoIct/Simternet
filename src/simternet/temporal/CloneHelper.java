package simternet.temporal;

/**
 * Static factory methods to clone objects which we cannot extend to implement
 * AsyncUpdate.
 * 
 * <p>
 * OPTIMIZE: Consider abandoning in favor of a different approach.
 * 
 * @see simternet.temporal.Temporal
 * @author kkoning
 * 
 */
public class CloneHelper {
	public static Double cloneDouble(Double obj) {
		return new Double(obj);
	}

	public static Integer cloneInteger(Integer obj) {
		return new Integer(obj);
	}

}
