package simternet.data.output;

import sim.util.Bag;
import simternet.agents.consumer.Consumer;
import simternet.engine.Simternet;

public class ConsumerDataReporter extends Reporter2 {

	private static final long					serialVersionUID	= 1L;

	public static final int						numFields;
	public static final String[]				headers;
	public static final String					filename			= "data/output/ConsumerData.out.csv";
	private static transient BufferedCSVWriter	csvWriter;

	static {
		ConsumerDataReporter.csvWriter = new BufferedCSVWriter(ConsumerDataReporter.filename);

		numFields = 2;
		headers = new String[ConsumerDataReporter.numFields];
		ConsumerDataReporter.headers[0] = "Consumer";
		ConsumerDataReporter.headers[1] = "Population";
		// ConsumerDataReporter2.headers[2] = "TransferRequested";
		// ConsumerDataReporter2.headers[3] = "TransferActual";

	}

	public ConsumerDataReporter(Simternet s) {
		super(ConsumerDataReporter.csvWriter, s);
	}

	@Override
	public void report() {
		Bag bag = s.getConsumerClasses().allObjects;

		for (int i = 0; i < bag.numObjs; i++) {
			Consumer c = (Consumer) bag.objs[i];
			Object values[] = new Object[ConsumerDataReporter.numFields];

			values[0] = c.name;
			values[1] = c.getPopulation().intValue();
			// values[2] = c.getTransferRequested();
			// values[3] = c.getTransferReceived();

			this.report(values);
		}

	}

	@Override
	public String[] getHeaders() {
		return ConsumerDataReporter.headers;
	}

	// @Override
	// public void collectData(SimState state) {
	// Simternet s = (Simternet) state;
	// Bag bag = s.getConsumerClasses().allObjects;
	// for (int i = 0; i < bag.numObjs; i++) {
	// Consumer c = (Consumer) bag.objs[i];
	// StringBuffer report = new StringBuffer();
	// report.append(c.toString().replace(',', '-')); // consumer id
	// report.append(Reporter.separater);
	// report.append(c.getPopulation());
	// report.append(Reporter.separater);
	// // report.append(c.transferRequested);
	// report.append(Reporter.separater);
	// // report.append(c.transferReceived);
	// report(report.toString());
	// }
	//
	// }
	//
	// @Override
	// public String getLogger() {
	// // TODO Auto-generated method stub
	// return "ConsumerData";
	// }
	//
	// @Override
	// public String getSpecificHeaders() {
	// return ConsumerDataReporter2.specificHeaders;
	// }

}
