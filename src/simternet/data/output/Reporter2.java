package simternet.data.output;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the new Reporter system for Simternet.
 * 
 * Each reporter may have zero or more "ReporterComponents", which are basically
 * prefixes to CSV files. This lets us have different levels of data reporting
 * (per-step, per-simulation, inside an evolutionary context, and outside)
 * without having to implement (and deal with elsewhere) a different reporter
 * class for each type.
 * 
 * Each ReporterComponent must return a String[] for getHeaders() and
 * getValues(). For each one of these components, the reporter object will query
 * for the list of headers or values, and prepend them (comma separated) to
 * whatever substantive data is logged by the specific Reporter.
 * 
 * Once a line of data is built, the Reporter writes it directly to a
 * BufferedCSVFile, rather than a log4j logger. This should allow the deployment
 * of more reporters without as much coordination/trouble because there will be
 * no need to separately configure each stream in log4j.properties. Each
 * BufferedCSVFile should only be written to by one <i>type</i> of reporter, but
 * an arbitrary number of reporters can write simultaneously to a single
 * BufferedCSVFile. This should allow efficient data reporting in a
 * multi-simternet evolutionary environment.
 * 
 * @author kkoning
 * 
 */
public abstract class Reporter2 implements Serializable {
	private static final long				serialVersionUID	= 1L;

	protected BufferedCSVWriter				csvWriter;
	protected ArrayList<ReporterComponent>	components			= new ArrayList<ReporterComponent>();
	boolean									tryPrintingHeaders	= true;

	public Reporter2(BufferedCSVWriter csvWriter) {
		this.csvWriter = csvWriter;
	}

	/**
	 * Triggered by the schedule or other entity which wants this reporter to
	 * collect and report its data.
	 */
	public abstract void report();

	protected void report(Object[] specificValues) {
		// The data to be written when we're done
		List<Object> allValues = new ArrayList<Object>();

		// If we're the first, we need to make sure headers are written.
		if (tryPrintingHeaders) {
			printHeaders();
		}

		// Prepend the values from the ReporterComponents
		for (ReporterComponent rc : components) {
			Object componentValues[] = rc.getValues();
			allValues.add(componentValues);
		}

		// Append the specificValues
		allValues.add(specificValues);

		// Build the string and write it to the file
		String csvLine = buildCSVLine(allValues);
		csvWriter.writeLine(csvLine);
	}

	public void addComponent(ReporterComponent rc) {
		components.add(rc);
	}

	private void printHeaders() {
		// Only print the headers if the CSV file has not yet had headers
		// written to it.
		if (csvWriter.headersWritten == false) {
			List<Object> headers = new ArrayList<Object>();

			// Prepend headers from each Reporter Component
			for (ReporterComponent rc : components) {
				for (String header : rc.getHeaders()) {
					headers.add(header);
				}
			}

			// Then add our headers, which we'll pull.
			for (String header : getHeaders()) {
				headers.add(header);
			}

			// Build and write the CSV String for the headers
			String headerString = buildCSVLine(headers);
			csvWriter.writeHeaders(headerString);
		}
		tryPrintingHeaders = false;
	}

	/**
	 * Return a list of the headers that are specific to this reporter. This is
	 * a "pull" operation because an indiviual reporter will not know when it is
	 * or is not appropriate to output headers.
	 * 
	 * @return A list of the headers specific to this reporter.
	 */
	public abstract String[] getHeaders();

	private final String buildCSVLine(List<Object> elements) {
		StringBuffer sb = new StringBuffer();
		Iterator<Object> oi = elements.iterator();
		while (oi.hasNext()) {
			Object thing = oi.next();
			String description = thing.toString();
			sb.append(description);
			if (oi.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

}
