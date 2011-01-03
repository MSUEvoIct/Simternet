package simternet.consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.AppCategory;
import simternet.application.ApplicationServiceProvider;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;
import simternet.temporal.Temporal;

public class ApplicationOptimizer extends SimpleConsumer {

	protected class AppBenefit {
		ApplicationServiceProvider app;
		Double benefit;
		Double cost;
		AbstractEdgeNetwork onNetwork;

		/**
		 * Reduces the benefit received if there is congestion
		 * 
		 * @return reduced benefit
		 */
		Double congest(Double benefit) {
			// Without a target network, assume zero congestion
			if (this.onNetwork == null)
				return benefit;
			// TODO: Document / parameterize this. Currently scales
			// benefit proportionally by congestion.
			Double congestionRatio = this.app
					.getCongestionRatio(this.onNetwork);
			if (congestionRatio == null)
				Logger.getRootLogger().log(Level.ERROR,
						"should have had a non-null congestion ratio");
			return benefit * congestionRatio;
		}

		Double density() {
			return this.congest(this.benefit) / this.cost;
		}

	}

	private static final long serialVersionUID = 1L;

	protected Temporal<Double> actualBenefit = new Temporal<Double>(0.0);

	protected Temporal<Double> expectedBenefit = new Temporal<Double>(0.0);

	public ApplicationOptimizer(Simternet s, Int2D location, Double population,
			ConsumerProfile profile) {
		super(s, location, population, profile);
		// TODO Auto-generated constructor stub
	}

	/*
	 * Consume the applications we've previously selected, once on each network.
	 */
	@Override
	protected void consumeApplications() {
		Collection<AbstractNetwork> localEdgeNetworks = this.s.getNetworks(
				null, AbstractEdgeNetwork.class, this.getLocation());

		for (List<ApplicationServiceProvider> asps : this.appsUsed.values())
			for (ApplicationServiceProvider asp : asps)
				for (AbstractNetwork network : localEdgeNetworks) {
					this.consumeApplication(asp, (AbstractEdgeNetwork) network);
					Logger.getRootLogger().log(Level.TRACE,
							this + " consumed " + asp);
				}
	}

	/**
	 * benefit = quality ^ alpha * expected congestion
	 * 
	 * @param asp
	 * @return
	 */
	protected Double getApplicationBenefit(ApplicationServiceProvider asp) {
		// TODO: Parameterize alpha
		Double preCongestionBenefit = Math.pow(asp.getQuality(), 0.7);
		return preCongestionBenefit;
	}

	/**
	 * Price is currently fixed at three in source; this should let them select
	 * two ASPs in each category.
	 * 
	 * TODO: Fix this.
	 * 
	 * @param appCategory
	 * @return
	 */
	protected Double getApplicationCategoryBudget(AppCategory appCategory) {
		// high enough to consume everything
		// TODO: Fix hard-coded value
		return 70.0;
	}

	protected Double getCost(ApplicationServiceProvider asp) {
		return asp.getPriceSubscriptions();
	}

	@Override
	protected void manageApplications() {

		// do the "greedy" algorithm
		for (AppCategory ac : AppCategory.values()) {
			List<AppBenefit> apps = new ArrayList<AppBenefit>();
			for (ApplicationServiceProvider asp : this.s.getASPs(ac)) {
				AppBenefit ab = new AppBenefit();
				ab.app = asp;
				ab.benefit = this.getApplicationBenefit(asp);
				ab.cost = this.getCost(asp);
				apps.add(ab);
			}
			// have benefits calculated. sort by density
			Collections.sort(apps, new Comparator<AppBenefit>() {

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
			// TODO: Use prior apps to compute benefits (learning) or
			// switching costs
			List<ApplicationServiceProvider> newASPs = new ArrayList<ApplicationServiceProvider>();
			this.appsUsed.put(ac, newASPs);

			// apps are now properly sorted. consume in order everything that
			// fits
			Double budget = this.getApplicationCategoryBudget(ac);
			for (AppBenefit ab : apps)
				if (ab.cost <= budget) {
					newASPs.add(ab.app);
					budget -= ab.cost;
				}

		}

	}

	@Override
	public void update() {
		super.update();
		this.expectedBenefit.update();
		this.actualBenefit.update();
	}

}
