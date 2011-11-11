package simternet.data.analysis;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ConsolidateData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options();

		Option dataDir = new Option("d", "dataDir", true, "Path to the Simternet data directory");
		dataDir.setRequired(true);
		options.addOption(dataDir);

		Option generations = new Option("g", "generations", true, "Keep generations xx-xx");
		options.addOption(generations);

		Option verbose = new Option("v", "verbose", false, "Verbose");
		options.addOption(verbose);

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		if (cmd.hasOption(verbose.getOpt())) {
			System.out.println("verbose was set");
		}

	}

}
