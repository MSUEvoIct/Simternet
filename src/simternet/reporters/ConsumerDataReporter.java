package simternet.reporters;

import sim.engine.SimState;
import sim.util.Bag;
import simternet.Simternet;
import simternet.consumer.Consumer;

public class ConsumerDataReporter extends Reporter {

	public static final String	specificHeaders	= "Consumer,Population,TransferRequested,TransferActual";

	static {
		new ConsumerDataReporter().logHeaders();
	}

	public ConsumerDataReporter() {
		super();
	}

	public ConsumerDataReporter(int i) {
		super(i);
	}

	public ConsumerDataReporter(Integer interval) {
		super(interval);
	}

	@Override
	public void collectData(SimState state) {
		Simternet s = (Simternet) state;
		Bag bag = s.getConsumerClasses().allObjects;
		for (int i = 0; i < bag.numObjs; i++) {
			Consumer c = (Consumer) bag.objs[i];
			StringBuffer report = new StringBuffer();
			report.append(c.toString().replace(',', '-')); // consumer id
			report.append(Reporter.separater);
			report.append(c.getPopulation());
			report.append(Reporter.separater);
			report.append(c.transferRequested);
			report.append(Reporter.separater);
			report.append(c.transferActual);
			this.report(report.toString());
		}

	}

	@Override
	public String getLogger() {
		// TODO Auto-generated method stub
		return "ConsumerData";
	}

	@Override
	public String getSpecificHeaders() {
		return ConsumerDataReporter.specificHeaders;
	}

}
