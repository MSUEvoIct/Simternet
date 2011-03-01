package simternet.consumer;

import simternet.application.AppCategory;

/**
 * A default consumer profile for desting purposes
 * 
 * @author kkoning
 * 
 */
public class DefaultConsumerProfile extends ConsumerProfile {

	@Override
	public Double getAppCategoryBudget(AppCategory ac) {
		return 15.0;
	}

}
