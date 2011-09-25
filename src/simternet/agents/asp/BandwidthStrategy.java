package simternet.agents.asp;

import java.io.Serializable;

public class BandwidthStrategy implements Serializable {

	protected static final long			serialVersionUID	= 1L;
	protected final ApplicationProvider	asp;

	public BandwidthStrategy(ApplicationProvider asp) {
		this.asp = asp;
	}

	public Double increaseBandwidth() {
		return this.asp.s.random.nextDouble() * 10;
	}

}
