package simternet.agents.consumer.behavior;

import simternet.agents.asp.AppCategory;
import simternet.agents.consumer.AppCategoryBudgetCalculator;
import simternet.agents.consumer.Consumer;
import simternet.engine.TraceConfig;

/**
 * Uses a static budget of 15.0 for each Application category.
 * 
 * @author kkoning
 * 
 */
public class DefaultAppCategoryBudgetCalculator extends AppCategoryBudgetCalculator {

	private static DefaultAppCategoryBudgetCalculator	singleton;

	public static DefaultAppCategoryBudgetCalculator getSingleton() {
		if (DefaultAppCategoryBudgetCalculator.singleton == null) {
			DefaultAppCategoryBudgetCalculator.singleton = new DefaultAppCategoryBudgetCalculator();
		}
		return DefaultAppCategoryBudgetCalculator.singleton;
	}

	@Override
	public Double calculateAppCategoryBudget(AppCategory appCat, Consumer c) {

		if (TraceConfig.kitchenSink) {
			TraceConfig.out.println(c + " calculated (fixed) budget " + 15 + " for AppCategory " + appCat);
		}

		return 15.0;
	}

}
