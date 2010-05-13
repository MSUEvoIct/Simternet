package simternet;

import java.io.Serializable;

/**
 * @author kkoning
 * 
 */
public enum PopulationDistribution implements Serializable {
	/**
	 * Each cell's population is evenly, randomly distributed between 0 and
	 * Exogenous.maxPopulation.
	 */
	RANDOM_FLAT
}