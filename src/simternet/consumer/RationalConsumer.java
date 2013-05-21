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
				
				if (edgeBenefits.size() > 0) {
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

					// If we have a current network, get those benefits
					EdgeBenefit currentBenefit = null;
					byte curNspID = consumer.nspUsed[x][y];
					boolean considerOldNSP = false;
					if (curNspID >= 0) {
						currentBenefit = consumer.getEdgeBenefit(curNspID, x, y);
						if (currentBenefit.surplus() > 0)
							considerOldNSP = true;
					}

					
					
					// make the consumption decision
					byte nspToUse = -1; // default no consumption
					
					if (considerOldNSP) {
						// XXX FIXME HARD CODED
						boolean keepOld = consumer.s.random.nextBoolean(0.5);
						if (keepOld) {
							nspToUse = curNspID;
						} else {
							nspToUse = topEdge.nspID;
						}
						
					} else {
						// make sure edge surplus is positive.
						if (topEdge.surplus() > 0)
							nspToUse = topEdge.nspID;
					}
					
					
					
					
					
					
					consumer.nspUsed[x][y] = nspToUse;					
				} else {
					consumer.nspUsed[x][y] = -1;  // no benefits, no use
				}
				

			}
		}

	}

}
