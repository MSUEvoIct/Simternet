package simternet.agents.asp;

import java.io.Serializable;

/**
 * Generic, deterministic Quality Investment Strategy.
 * 
 * @author kkoning
 * 
 */
public class QualityStrategy implements Serializable {

	protected final ApplicationProvider	asp;
	private static final long			serialVersionUID	= 1L;

	public QualityStrategy(ApplicationProvider asp) {
		this.asp = asp;
	}

	/**
	 * For now, just increase quality by a small, random number. TODO: A
	 * non-rediculous heuristic
	 * 
	 */
	public void investInQuality() {
		Double toInvest = this.asp.s.random.nextDouble() * 10;
		Quality.increaseQuality(this.asp, toInvest);
	}

}
