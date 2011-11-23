package simternet.data.output;

import sim.util.Bag;
import simternet.agents.consumer.Consumer;
import simternet.engine.Simternet;
import simternet.network.EdgeNetwork;

public class AggregateConsumerDataReporter extends Reporter {
	private static final long		serialVersionUID	= 1L;

	public static final int			numFields;
	public static final String[]	headers;
	public static final String		filename			= "AggregateConsumerData";

	static {
		numFields = 6;
		headers = new String[AggregateConsumerDataReporter.numFields];
		AggregateConsumerDataReporter.headers[0] = "NumNetworkUsers";
		AggregateConsumerDataReporter.headers[1] = "PaidToNSPs";
		AggregateConsumerDataReporter.headers[2] = "NumAppSubscriptions";
		AggregateConsumerDataReporter.headers[3] = "BenefitReceived";
		AggregateConsumerDataReporter.headers[4] = "TransferRequested";
		AggregateConsumerDataReporter.headers[5] = "TransferReceived";

	}

	public AggregateConsumerDataReporter(Simternet s) {
		super(s);
	}

	@Override
	public void report() {
		Bag bag = s.getConsumerClasses().allObjects;
		Object values[] = new Object[AggregateConsumerDataReporter.numFields];

		int numNetworkUsers = 0;
		double paidToNSPs = 0.0;
		int numAppSubscriptions = 0;
		double benefitReceived = 0.0;
		double transferRequested = 0.0;
		double transferReceived = 0.0;

		for (int i = 0; i < bag.numObjs; i++) {
			Consumer c = (Consumer) bag.objs[i];

			if (c != null) {
				EdgeNetwork cur = c.getEdgeNetwork().get();
				if (cur != null) {
					numNetworkUsers++;
					paidToNSPs += cur.getPrice();
				}
				numAppSubscriptions += c.getNumAppsUsed();
				benefitReceived += c.benefitReceived.get();
				transferRequested += c.transferRequested.get();
				transferReceived += c.transferReceived.get();
			}

		}

		values[0] = numNetworkUsers;
		values[1] = paidToNSPs;
		values[2] = numAppSubscriptions;
		values[3] = benefitReceived;
		values[4] = transferRequested;
		values[5] = transferReceived;

		this.report(values);

	}

	@Override
	public String[] getHeaders() {
		return AggregateConsumerDataReporter.headers;
	}

	@Override
	public String getFileName() {
		return AggregateConsumerDataReporter.filename;
	}
}
