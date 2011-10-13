package simternet.data.output;

import java.io.Serializable;

/**
 * Adds a prefix to a CSV file. This allows, e.g., reporters which include a
 * generation and chunk, or a step, or both.
 * 
 * @author kkoning
 * 
 */
public abstract class ReporterComponent implements Serializable {
	private static final long	serialVersionUID	= 1L;

	public abstract String[] getHeaders();

	public abstract Object[] getValues();

}
