package simternet.agents.consumer.behavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import simternet.agents.asp.AppCategory;
import simternet.agents.asp.ApplicationProvider;
import simternet.agents.consumer.AppBenefitCalculator;
import simternet.agents.consumer.AppManager;
import simternet.agents.consumer.Consumer;
import simternet.agents.consumer.Consumer.AppBenefit;
import simternet.engine.TraceConfig;
import simternet.network.EdgeNetwork;

public class GreedyAppManager implements AppManager {

	private static final long		serialVersionUID	= 1L;
	private static GreedyAppManager	singleton;

	public static GreedyAppManager getSingleton() {
		if (GreedyAppManager.singleton == null) {
			GreedyAppManager.singleton = new GreedyAppManager();
		}
		return GreedyAppManager.singleton;
	}

	@Override
	public void manageApplications(Consumer c) {

		if (TraceConfig.kitchenSink) {
			TraceConfig.out.println(this + " managing applications for " + c);
		}

		// do the "greedy" algorithm
		for (AppCategory ac : AppCategory.values()) {
			// calculate the expected benefit from each application in category
			List<Consumer.AppBenefit> appBenefits = new ArrayList<Consumer.AppBenefit>();

			for (ApplicationProvider asp : c.s.getASPs(ac)) {
				AppBenefitCalculator abc = c.getAppBenefitCalculator();

				// Will describe our benefit
				Consumer.AppBenefit ab = new Consumer.AppBenefit();

				ab.asp = asp;

				// looks at congestion of ASP on the Edge Network we're on
				EdgeNetwork en = c.getEdgeNetwork().getFuture();
				ab.benefit = abc.estimateBenefit(c, asp, en);
				ab.cost = asp.getPriceSubscriptions();
				appBenefits.add(ab);
			}

			// sort those benefits by density (benefit/cost)
			Collections.sort(appBenefits, new Comparator<Consumer.AppBenefit>() {

				@Override
				public int compare(Consumer.AppBenefit o1, Consumer.AppBenefit o2) {
					if (o1.density() < o2.density())
						return 1;
					if (o1.density() > o2.density())
						return -1;
					return 0;
				}
			});

			// clear out our current applications, "start from scratch"
			List<ApplicationProvider> newASPs = new ArrayList<ApplicationProvider>();

			// apps are now properly sorted. consume in order everything that
			// fits
			Double budget = c.getApplicationCategoryBudget(ac);
			for (AppBenefit ab : appBenefits)
				if (ab.cost <= budget) {
					newASPs.add(ab.asp);
					budget -= ab.cost;
				}

			if (TraceConfig.kitchenSink) {
				TraceConfig.out
						.println("After consuming apps with highest positive benefit density in descending order, " + c
								+ " has remaining budget in AppCategory " + ac + " of " + budget);
			}

			// Put our new list object into the structure used to actualize
			// consumption.
			c.setAppsUsed(ac, newASPs);

		}

	}

}
