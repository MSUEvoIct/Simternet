package simternet.application;

/**
 * Generic, deterministic Quality Investment Strategy.
 * 
 * @author kkoning
 * 
 */
public class QualityStrategy {

	protected final ApplicationProvider	asp;

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
