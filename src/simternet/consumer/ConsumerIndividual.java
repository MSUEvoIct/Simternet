package simternet.consumer;

import simternet.Simternet;

public interface ConsumerIndividual {

	void manageApplications(Consumer consumer, Simternet s);
	void manageNetworks(Consumer consumer, Simternet s);

}
