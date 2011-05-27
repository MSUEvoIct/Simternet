package simternet.reporters;

import simternet.Simternet;

public interface Reporter {
	public String getFileName();

	public void report(DataCollector dc, Simternet s);

}
