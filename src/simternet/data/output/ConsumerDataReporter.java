package simternet.data.output;

import sim.util.Bag;
import simternet.agents.consumer.Consumer;
import simternet.engine.Simternet;

public class ConsumerDataReporter extends Reporter2 {
	private static final long		serialVersionUID	= 1L;

	public static final int			numFields;
	public static final String[]	headers;
	public static final String		filename			= "ConsumerData";

	static {
		numFields = 2;
		headers = new String[ConsumerDataReporter.numFields];
		ConsumerDataReporter.headers[0] = "Consumer";
		ConsumerDataReporter.headers[1] = "Population";
		// ConsumerDataReporter2.headers[2] = "TransferRequested";
		// ConsumerDataReporter2.headers[3] = "TransferActual";

	}

	public ConsumerDataReporter(Simternet s) {
		super(s);
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

	@Override
	public String getFileName() {
		return ConsumerDataReporter.filename;
	}
}
