package simternet.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import simternet.Simternet;
import simternet.WeightedIndex;
import simternet.consumer.Consumer.AppBenefit;
import simternet.consumer.Consumer.EdgeBenefit;
import ec.agency.NullIndividual;

public class RationalConsumer extends NullIndividual implements
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
					Collections.sort(edgeBenefits, Collections.reverseOrder(
							new Comparator<Consumer.EdgeBenefit>() {
								@Override
								public int compare(Consumer.EdgeBenefit o1,
										Consumer.EdgeBenefit o2) {
									if (o1.surplus() > o2.surplus())
										return 1;
									if (o1.surplus() < o2.surplus())
										return -1;
									return 0;
								}
							}));
					byte curNspID = consumer.nspUsed[x][y];
					consumer.nspUsed[x][y] = selectNSP(curNspID, edgeBenefits, s);
				} else {  //There are no edge benefits
					consumer.nspUsed[x][y] = Consumer.NONSP;
				}
			} // y
		} // x
	}
	
	
	protected byte selectNSP(byte curNSPID, List<EdgeBenefit> revSortedEdgeBenefits, Simternet s){
		EdgeBenefit topCandidate = revSortedEdgeBenefits.get(0);
		double topSurplus = topCandidate.surplus();
		if (topSurplus < 0){ //Don't use any NSP if there is no positive surplus
			return Consumer.NONSP;
		}
		if (revSortedEdgeBenefits.size() == 0){ //If there are no NSPs in this list, can't pick any
			return Consumer.NONSP;
		}
		if (revSortedEdgeBenefits.size() == 1){ //If there is only one candidate with positive surplus, pick it
			return revSortedEdgeBenefits.get(0).nspID;
		}
		
		//Otherwise, with a positive top surplus, weight all candidate edge networks
		//based on their location within the candidate threshold (a percent of the top surplus)
		//and select randomly from the weighted values.
		double candidateRange = topSurplus * s.nspCandidateFracOfBestThreshold;
		double minCandidateSurplus = topSurplus - candidateRange;
		WeightedIndex<EdgeBenefit> bucket = new WeightedIndex<EdgeBenefit>();
		for (byte k=0; k<revSortedEdgeBenefits.size(); k++){
			EdgeBenefit candidate = revSortedEdgeBenefits.get(k);
			double candidateSurplus = candidate.surplus();
			double candidateDelta = topSurplus - candidateSurplus;
			if (candidate.nspID == curNSPID){
				double weight = 1-(candidateDelta/candidateRange)+s.nspIncumbentAdditiveAdvantage;
				if (weight > 0)
					bucket.add(candidate, weight);
			} else if (candidateSurplus >= minCandidateSurplus){
				double weight = 1-(candidateDelta/candidateRange);
				bucket.add(candidate, weight);
			} 
		}
		return bucket.get(s.random.nextDouble()*bucket.totalWeight()).nspID;
	}
	
}
