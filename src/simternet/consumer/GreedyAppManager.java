package simternet.consumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import simternet.application.AppCategory;
import simternet.application.ApplicationProvider;

public class GreedyAppManager extends AppManager implements Serializable {

	private static final long		serialVersionUID	= 1L;
	private static GreedyAppManager	singleton;

	public static GreedyAppManager getSingleton() {
		if (GreedyAppManager.singleton == null)
			GreedyAppManager.singleton = new GreedyAppManager();
		return GreedyAppManager.singleton;
	}

	@Override
	public void manageApplications(Consumer c) {

		// do the "greedy" algorithm
		for (AppCategory ac : AppCategory.values()) {
			// calculate the expected benefit from each application in category
			List<AppBenefit> appBenefits = new ArrayList<AppBenefit>();

			for (ApplicationProvider asp : c.s.getASPs(ac)) {

				AppBenefit ab = new AppBenefit();
				ab.app = asp;
				ab.benefit = c.getAppBenefitCalculator().congestedBenefit(c, asp,
						asp.getDatacenter().getCongestionRatio(c.edgeNetwork.get()));
				ab.cost = asp.getPriceSubscriptions();
				appBenefits.add(ab);
			}

			// sort those benefits by density (benefit/cost)
			Collections.sort(appBenefits, new Comparator<AppBenefit>() {

				@Override
				public int compare(AppBenefit o1, AppBenefit o2) {
					if (o1.density() < o2.density())
						return 1;
					if (o1.density() > o2.density())
						return -1;
					return 0;
				}
			});

			// clear out our current applications, "start from scratch"
			List<ApplicationProvider> newASPs = new ArrayList<ApplicationProvider>();
			// Put our new list object into the structure used to actualize
			// consumption.
			c.appsUsed.put(ac, newASPs);

			// apps are now properly sorted. consume in order everything that
			// fits
			Double budget = c.profile.getAppCategoryBudget(ac);
			for (AppBenefit ab : appBenefits)
				if (ab.cost <= budget) {
					newASPs.add(ab.app);
					budget -= ab.cost;
				}
		}

	}

}
