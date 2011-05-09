package simternet.application;

/**
 * Quality utility class. Contains methods related to ASP's cost of increasing
 * quality.
 * 
 * @author kkoning
 * 
 */
public class Quality {

	public static Double getQualityCost(ApplicationProvider asp, Double qualityAmount) {
		// TODO: Think about real cost function for quality improvements.
		Double amount = 0.0;
		amount += Math.pow(asp.getQuality(), 1 / 3);
		amount += Math.pow(qualityAmount, 2);
		return amount;
	}

	public static void increaseQuality(ApplicationProvider asp, Double amount) {
		Double amountAvailable = asp.financials.getAvailableFinancing();
		Double cost = Quality.getQualityCost(asp, amount);
		if (cost < amountAvailable) {
			asp.financials.capitalize(cost);
			asp.quality.increase(amount);
		}
	}

}
