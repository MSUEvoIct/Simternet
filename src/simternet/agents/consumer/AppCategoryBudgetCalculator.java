package simternet.agents.consumer;

import java.io.Serializable;

import simternet.agents.asp.AppCategory;

public abstract class AppCategoryBudgetCalculator implements Serializable {

	private static final long	serialVersionUID	= 1L;

	public abstract Double calculateAppCategoryBudget(AppCategory cat, Consumer c);

	public void calculateAppCategoryBudgets(Consumer c) {
		for (AppCategory cat : AppCategory.values()) {
			Double catBudget = calculateAppCategoryBudget(cat, c);
			if (catBudget != null) {
				setAppCategoryBudget(c, cat, catBudget);
			}
		}
	}

	public void setAppCategoryBudget(Consumer c, AppCategory appCat, Double budget) {
		c.appBudgetConstraints.put(appCat, budget);
	}

}
