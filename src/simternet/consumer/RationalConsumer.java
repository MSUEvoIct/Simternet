package simternet.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import simternet.Simternet;
import simternet.consumer.Consumer.AppBenefit;
import simternet.consumer.Consumer.EdgeBenefit;
import ec.vector.FloatVectorIndividual;

public class RationalConsumer extends FloatVectorIndividual implements
		ConsumerIndividual {
	private static final long serialVersionUID = 1L;

	@Override
	public void manageApplications(Consumer consumer, Simternet s) {
		// Once for each location
		for (byte x = 0; x < s.landscapeSizeX; x++) {
			for (byte y = 0; y < s.landscapeSizeY; y++) {

				List<Consumer.AppBenefit> appBenefits;
				appBenefits = consumer.getAllASPBenefits(
						consumer.nspUsed[x][y], x, y);

				// Sort them by density benefit
				Collections.sort(appBenefits,
						new Comparator<Consumer.AppBenefit>() {

							@Override
							public int compare(Consumer.AppBenefit o1,
									Consumer.AppBenefit o2) {
								if (o1.density() < o2.density())
									return 1;
								if (o1.density() > o2.density())
									return -1;
								return 0;
							}
						});

				// Consume by higher density until budget exhausted
				List<Byte> aspsChosen = new ArrayList<Byte>();
				double budget = consumer.aspBudgetConstraint;
				for (AppBenefit ab : appBenefits) {
					if (ab.cost <= budget) {
						aspsChosen.add(ab.aspID);
						budget -= ab.cost;
					}
				}

				consumer.aspSubscriptions[x][y] = aspsChosen;
			}
		}

	}

	@Override
	public void manageNetworks(Consumer consumer, Simternet s) {
		// Once for each location
		for (byte x = 0; x < s.landscapeSizeX; x++) {
			for (byte y = 0; y < s.landscapeSizeY; y++) {

				// Get list of benefits for edge networks
				List<EdgeBenefit> edgeBenefits = consumer.getEdgeBenefitsAt(x, y);
				
				
				// Sort by consumer surplus
				EdgeBenefit topEdge = Collections.max(edgeBenefits,
						new Comparator<Consumer.EdgeBenefit>() {

							@Override
							public int compare(Consumer.EdgeBenefit o1,
									Consumer.EdgeBenefit o2) {
								if (o1.surplus() < o2.surplus())
									return 1;
								if (o1.surplus() > o2.surplus())
									return -1;
								return 0;
							}
						});
				
				byte nspToUse = topEdge.nspID;
				consumer.nspUsed[x][y] = nspToUse;
			}
		}

	}

}
