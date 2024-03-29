 package simternet.consumer;

import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.Simternet;
import simternet.asp.ASP;
import simternet.network.EdgeNetwork;
import simternet.nsp.NSP;

/**
 * Consumers must be initialized last, as they query Simternet for the number of
 * NSPs and ASPs (necessary for the consumers' data structures)
 * 
 * @author kkoning
 * 
 */
public class Consumer implements Steppable {
	private static final long serialVersionUID = 1L;

	public static final byte NONSP = -1;
	
	Simternet s;
	public byte id;
	public ConsumerIndividual ind;

	public float[][] population;

	/** Randomly assigned [0..1] preference factor */
	public double preference;

	/**
	 * Budget limit for applications TODO: get rational value
	 */
	public double aspBudgetConstraint;

	public double totalSurplus = 0;

	/**
	 * The number of individuals that subscribe to an NSP's edge
	 */
	public byte[][] nspUsed;

	/**
	 * Each object is a byte[] with a list of ASPs (by aspID) used by this
	 * consumer.
	 */
	public List<Byte>[][] aspSubscriptions;

	public static class AppBenefit {
		public byte aspID;
		public double cost;
		public double benefit;

		public double density() {
			if (cost <= 0)
				cost = Float.MIN_NORMAL;
			return benefit / cost;
		}

		public double surplus() {
			return benefit - cost;
		}
	}

	public static class EdgeBenefit {
		public byte nspID;
		public double sumAppBenefits;
		public double cost;
		public double wtpExponent;

		public double surplus() {
			double wtp = Math.pow(sumAppBenefits, wtpExponent);
			double surplus = wtp - cost;
			
			if (Double.isNaN(surplus) || Double.isInfinite(surplus))
				return 0;
			
			return surplus;
		}
	}

	@SuppressWarnings("unchecked")
	public Consumer(Simternet s, ConsumerIndividual ind, byte id) {
		this.s = s;
		this.ind = ind;
		this.id = id;
		s.populationInitializer.populate(s, this);
		this.nspUsed = new byte[s.landscapeSizeX][s.landscapeSizeY];
		for (byte x = 0; x < s.landscapeSizeX; x++)
			for (byte y = 0; y < s.landscapeSizeY; y++)
				this.nspUsed[x][y] = -1; // start out with no NSP
		this.aspSubscriptions = new List[s.landscapeSizeX][s.landscapeSizeY];
		this.aspBudgetConstraint = s.appBudget + s.appBudgetStdDev
				* s.random.nextGaussian();
		this.preference = s.random.nextDouble();

	}

	public AppBenefit getASPBenefit(byte aspID, byte nspID, byte x, byte y) {
		AppBenefit toReturn = new AppBenefit();

		ASP asp = s.allASPs[aspID];
		double qualTerm = Math.pow(asp.getQuality(), s.qualityExponent);
		double prefDiff = Math.abs(this.preference - asp.specialization);
		double prefTerm = Math.pow(prefDiff, s.preferenceExponent);
		double congest = 0;
		if (nspID >= 0)
//			if (s.allNSPs[nspID].edgeNetworks[x][y] != null)
				congest = s.allNSPs[nspID].edgeNetworks[x][y].congestion[aspID];
		double congestTerm = 1 - congest;

		toReturn.aspID = aspID;
		toReturn.benefit = qualTerm * prefTerm * congestTerm;
		toReturn.cost = s.allASPs[aspID].price;

		return toReturn;
	}

	public List<AppBenefit> getAllASPBenefits(byte nspID, byte x, byte y) {
		List<AppBenefit> toReturn = new ArrayList<AppBenefit>();
		// Check all ASPs
		for (byte aspID = 0; aspID < s.allASPs.length; aspID++) {
			AppBenefit benefit = getASPBenefit(aspID, nspID, x, y);
			toReturn.add(benefit);
		}
		return toReturn;
	}

	/**
	 * Uses existing set of applications
	 * 
	 * @param x
	 * @param y
	 * @return list of total benefits for each edge at location
	 */
	public List<EdgeBenefit> getEdgeBenefitsAt(byte x, byte y) {
		List<EdgeBenefit> toReturn = new ArrayList<EdgeBenefit>();

		// Potentially one for each NSP at this location
		for (byte nspID = 0; nspID < s.allNSPs.length; nspID++) {
			EdgeBenefit eb = null;
			if (s.allNSPs[nspID].edgeNetworks[x][y] != null)
				eb = getEdgeBenefit(nspID, x, y);
			if (eb != null)
				toReturn.add(eb);
		}

		return toReturn;
	}

	EdgeBenefit getEdgeBenefit(byte nspID, byte x, byte y) {
		List<Byte> aspsUsed = aspSubscriptions[x][y];

		EdgeNetwork en = s.allNSPs[nspID].edgeNetworks[x][y];
		if (en != null) {
			EdgeBenefit eb = new EdgeBenefit();
			eb.cost = en.price;
			eb.wtpExponent = s.wtpExponent;
			eb.nspID = nspID;
			for (Byte aspID : aspsUsed) {
				AppBenefit ab = getASPBenefit(aspID, nspID, x, y);
				eb.sumAppBenefits += ab.benefit;
			}
			return eb;
		} else {
			return null;
		}
	}

	/**
	 * @param x
	 * @param y
	 * @param nspID
	 * @return # of subscribers to NSP's edge at specified location
	 */
	public float getNSPSubscribers(int x, int y, byte nspID) {
		if (nspUsed[x][y] == nspID)
			return population[x][y];
		else
			return 0;
	}

	/**
	 * @param nspID
	 * @return # of subscribers to NSP at all locations
	 */
	public float getNSPSubscribers(byte nspID) {
		float numCustomers = 0;
		for (Int2D loc : s.getAllLocations()) {
			numCustomers += getNSPSubscribers(loc.x, loc.y, nspID);
		}
		return numCustomers;
	}

	/**
	 * @param x
	 * @param y
	 * @param aspID
	 * @return # of subscriptions so ASPs at specified location
	 */
	public float getASPSubscribers(int x, int y, byte aspID) {
		// If there is no NSP at this location, there should be zero ASP
		// subscriptions
		if (nspUsed[x][y] < 0)
			return 0;

		List<Byte> aspsUsed = aspSubscriptions[x][y];
		// If ASP is in the list of ones used...
		for (Byte b : aspsUsed) {
			if (b == aspID)
				return population[x][y];
		}
		// otherwise, it is not used at all.
		return 0;
	}

	/**
	 * @param aspID
	 * @return # of ASP Subscribers at all locations
	 */
	public double getASPSubscribers(byte aspID) {
		double numCustomers = 0;
		for (Int2D loc : s.getAllLocations()) {
			numCustomers += getASPSubscribers(loc.x, loc.y, aspID);
		}
		return numCustomers;
	}

	public double getASPNSPSubscribers(byte nspID, byte aspID) {
		double numSubscribers = 0;
		for (Int2D loc : s.getAllLocations()) {
			if (nspUsed[loc.x][loc.y] == nspID)
				numSubscribers += getASPSubscribers(loc.x,loc.y,aspID);
		}
		return numSubscribers;
	}
	
	@Override
	public String toString() {
		return "Consumer" + id;
	}

	@Override
	public void step(SimState state) {
		// Need to unsubscribe from edge networks if the NSP went bankrupt and it's no longer there
		for (Int2D loc : s.getAllLocations()) {
			byte nspID = nspUsed[loc.x][loc.y];
			if (nspID >= 0) {
				if (s.allNSPs[nspID].edgeNetworks[loc.x][loc.y]== null ) {
					nspUsed[loc.x][loc.y] = -1;
				}
				
			}
		}
		
		ind.manageApplications(this, s);
		ind.manageNetworks(this, s);

		this.consumeNetworks();
		this.consumeApplications();

	}

	void consumeNetworks() {
		for (byte x = 0; x < s.landscapeSizeX; x++) {
			for (byte y = 0; y < s.landscapeSizeY; y++) {
				byte nspUsed = this.nspUsed[x][y];
				if (nspUsed >= 0) {
					NSP nsp = s.allNSPs[nspUsed];
					EdgeNetwork en = nsp.edgeNetworks[x][y];
					en.receivePayment(this.id, this.population[x][y]);

					// Record surplus
					EdgeBenefit eb = getEdgeBenefit(nspUsed, x, y);
					this.totalSurplus += eb.surplus();
					
					if (this.totalSurplus == Double.NaN)
						throw new RuntimeException("Consumer surplus is now NaN? Debug me");

					// TODO: have consumers record price paid (* population)

				}

			}
		}
	}

	void consumeApplications() {
		for (byte x = 0; x < s.landscapeSizeX; x++) {
			for (byte y = 0; y < s.landscapeSizeY; y++) {
				// Only consume if we have a network
				if (nspUsed[x][y] >= 0) {
					for (Byte aspID : this.aspSubscriptions[x][y]) {
						ASP asp = s.allASPs[aspID];
						asp.customerUse(this.id, this.population[x][y], x, y,
								this.nspUsed[x][y]);
						// TODO track usage on consumer side?
					}
				}

			}
		}
	}

}
