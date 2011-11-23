package simternet.data.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import simternet.engine.Simternet;

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
public abstract class Reporter implements Serializable {
	private static final long				serialVersionUID	= 1L;

	protected ArrayList<ReporterComponent>	components			= new ArrayList<ReporterComponent>();
	boolean									tryPrintingHeaders	= true;
	protected Simternet						s;
	protected transient PrintWriter			writer;
	private static final int				bufferSize			= 100000;
	// output every n
	public int								stepModulo			= 1;

	public abstract String getFileName();

	public Reporter(Simternet s) {
		this.s = s;
	}

	/**
	 * Triggered by the schedule or other entity which wants this reporter to
	 * collect and report its data.
	 */
	public abstract void report();

	public void finish() {
		if (writer != null) {
			writer.flush();
			writer.close();
		}
	}

	private void initPrintWriter() {
		// get directory to output to
		DecimalFormat df = new DecimalFormat();
		df.setMinimumIntegerDigits(3);

		String directory = getOutputDirectory() + "/PerStep/gen-" + df.format(s.generation) + "/";

		File outputDir = new File(directory);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		String fileName = directory + getFileName() + ".chunk-" + df.format(s.chunk) + ".csv";
		File outputFile = new File(fileName);

		// create a buffered printwriter for us to use.
		FileWriter fw;
		BufferedWriter bw;
		try {
			fw = new FileWriter(outputFile);
			bw = new BufferedWriter(fw, Reporter.bufferSize);
			writer = new PrintWriter(bw);
			printHeaders();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(0);
		}

		if (writer == null) {
			System.out.println("Why can't I open the file?");
		}

	}

	private final PrintWriter getWriter() {
		if (writer == null) {
			initPrintWriter();
		}

		return writer;
	}

	protected void report(Object[] specificValues) {

		// The data to be written when we're done
		List<Object> allValues = new ArrayList<Object>();

		// Prepend the values from the ReporterComponents
		for (ReporterComponent rc : components) {
			Object componentValues[] = rc.getValues();
			for (Object o : componentValues) {
				allValues.add(o);
			}
		}

		// Append the specificValues
		for (Object o : specificValues) {
			allValues.add(o);
		}

		// Build the string and write it to the file
		String csvLine = buildCSVLine(allValues);
		getWriter().println(csvLine);
	}

	public void addComponent(ReporterComponent rc) {
		components.add(rc);
	}

	private void printHeaders() {
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
		getWriter().println(headerString);
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

	protected String getOutputDirectory() {
		String outputDir;

		Map<String, String> env = System.getenv();
		String envOutputDir = env.get("SIMTERNET_OUTPUT");
		if (envOutputDir != null) {
			outputDir = envOutputDir;
		} else {
			outputDir = "data/output";
		}

		return outputDir;
	}

}
