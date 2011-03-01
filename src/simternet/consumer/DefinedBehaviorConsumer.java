package simternet.consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.AppCategory;
import simternet.application.ApplicationServiceProvider;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.temporal.Temporal;

/**
 * In this class we're specifically defining the behavior of consumers, rather
 * than having it be determined by the ouput of an evolutionary algorithm.
 * 
 * @author kkoning
 * 
 */
public class DefinedBehaviorConsumer extends AbstractConsumerClass {

	private static final long	serialVersionUID	= 1L;

	protected Temporal<Double>	actualBenefit		= new Temporal<Double>(0.0);
	protected Temporal<Double>	expectedBenefit		= new Temporal<Double>(0.0);

	public DefinedBehaviorConsumer(Simternet s, Int2D location, Double population, ConsumerProfile profile) {
		super(s, location, population, profile);
	}

	@Override
	protected void manageApplications() {

		// do the "greedy" algorithm
		// in each application category
		for (AppCategory ac : AppCategory.values()) {
			// calculate the expected benefit from each application in that
			// category

			// TODO: Use prior apps to compute benefits (learning) or
			// switching costs
			List<AppBenefit> apps = new ArrayList<AppBenefit>();
			for (ApplicationServiceProvider asp : this.s.getASPs(ac)) {
				AppBenefit ab = new AppBenefit();
				ab.app = asp;
				ab.benefit = this.profile.getApplicationBenefit(asp);
				ab.cost = asp.getPriceSubscriptions();
				apps.add(ab);
			}

			// sort those benefits by density (benefit/cost)
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
			List<ApplicationServiceProvider> newASPs = new ArrayList<ApplicationServiceProvider>();
			this.appsUsed.put(ac, newASPs);

			// apps are now properly sorted. consume in order everything that
			// fits
			Double budget = this.profile.getAppCategoryBudget(ac);
			for (AppBenefit ab : apps)
				if (ab.cost <= budget) {
					newASPs.add(ab.app);
					budget -= ab.cost;
				}
		}

	}

	@Override
	protected void manageNetworks() {
		Collection<Network> c = this.s.getNetworks(null, EdgeNetwork.class, this.location);
		double lowestPrice = Double.MAX_VALUE;
		EdgeNetwork lowestPricedNetwork = null;
		for (Network an : c) {
			EdgeNetwork aen = (EdgeNetwork) an;
			double price = aen.getPrice();
			if (price < lowestPrice) {
				lowestPrice = price;
				lowestPricedNetwork = aen;
			}
		}
		if (lowestPrice < this.profile.getMaxNetworkPrice())
			this.subscribedTo.set(lowestPricedNetwork);
		else
			this.subscribedTo.set(null);

	}

	@Override
	public void update() {
		super.update();
		this.expectedBenefit.update();
		this.actualBenefit.update();
	}

}
