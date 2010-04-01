package simternet;

/**
 * @author kkoning
 * 
 * This class contains all exogenous model variables.
 *
 */
public class Exogenous {

	/**
	 *  Version specification is necessary to use persisted states 
	 *  across different versions of the model as it evolves.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final double netCostSimpleArea = 10000;
	public static final double netCostSimpleUser = 10;
	public static final double nspEndowment = 11000;
	public static final double paybackRate = 0.05;
	public static final double interestRate = 0.07;
	public static final int landscapeX = 3;
	public static final int landscapeY = 3;
	public static final double maxPopulation = 1000;
	
	public static final double maxPrice = 200;
	public static final double closeEnoughPrice = 0.1;
	public static final double proportionChange = 0.3;
	
	
	public static final PopulationDistribution defaultPopulationDistribution = PopulationDistribution.RANDOM_FLAT;

}
