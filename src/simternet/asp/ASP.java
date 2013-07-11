package simternet.asp;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.BankruptcyException;
import simternet.Financials;
import simternet.Simternet;
import simternet.consumer.Consumer;
import simternet.network.DataCenter;
import simternet.network.NetFlow;
import simternet.network.Network;

public class ASP implements Steppable {
	private static final long serialVersionUID = 1L;

	public byte id;

	// Financial condition of this ASP
	public Financials financials = new Financials();
	private boolean bankrupt = false;

	/** Represents the investment made in the usefulness of the application */
	double quality;

	public double getQuality() {
		return quality;
	}

	/** From [0..1]; closer to specialization of consumer -> higher utility */
	public double specialization;

	/**
	 * The price the ASP extracts from users for the use of the application, be
	 * it in direct payments, attention paid to advertising, etc...
	 */
	public double price;

	/** The amount of bandwidth consumed per user */
	double bandwidth;

	/** Network Operations */
	public DataCenter datacenter;

	/** Reference to the main simulation object */
	public Simternet s;

	/** The individual/agent controlling the ASP */
	public ASPIndividual ind;

	public ASP(Simternet s, ASPIndividual ind, byte aspID) {
		this.s = s;
		this.ind = ind;
		this.id = aspID;
		this.datacenter = new DataCenter(this);
	}

	public double getCustomers() {
		double numCustomers = 0;
		for (Consumer c : s.allConsumers) {
			numCustomers += c.getASPSubscribers(id);
		}
		return numCustomers;
	}

	@Override
	public void step(SimState state) {
		
		if (bankrupt)
			return;
		
		// Set quality
		double qualToAdd = ind.improveQuality(new QualityStimulus(this));
		if (qualToAdd < 0)
			qualToAdd = 0;
		
		this.improveQuality(qualToAdd);

		// Set price
		price = ind.setPrice(new PriceStimulus(this));
		if (price < 0)
			price = 0;

		// Update bandwidth
		bandwidth = Math.pow(quality, s.qualityToBandwidthExponent);
		
		// Buy backbone bandwidth from NSPs
		for (int nspID = 0; nspID < s.allNSPs.length; nspID++) {
			
			// Construct stimuli
			BackbonePurchaseStimulus bps = new BackbonePurchaseStimulus();
			bps.nspID = nspID;
			
			// If price controlled, hard code the price to what was configured
			if (s.policyPriceControlBackbone)
				bps.price = s.policyPriceBackbone;
			else
				bps.price = s.allNSPs[nspID].getASPTransitPrice(this.id);
			
			bps.aspCustomers = getCustomers();
			bps.nspCustomers = s.allNSPs[nspID].getCustomers();
			bps.intersectionCustomers = getCustomers(nspID);
			bps.totalPopulation = s.getTotalPopulation();
			
			// Submit stimuli, process
			//
			double bwToPurchase = ind.buyBandwidth(bps);
			if (bwToPurchase < 0)
				bwToPurchase = 0;
			
			Network nspBackbone = s.allNSPs[nspID].backbone;
			
			// Now record (both sides of) this transaction.
			double totalBandwidthBill = bwToPurchase*bps.price;
			
			try {
				this.financials.payExpense(totalBandwidthBill);
			} catch (BankruptcyException e) {
				goBankrupt();
				return;
			}
			
			s.allNSPs[nspID].financials.earnRevenue(totalBandwidthBill);

			//  Actually set the bandwidth
			datacenter.setEgressBandwidth(nspBackbone, bwToPurchase);
			
			// track this data for stats purposes
			s.avgBackbonePrice.increment(bps.price);
			s.avgBackbonePurchaseQty.increment(bwToPurchase);
			
		}
		
		
		// Operate Network
		datacenter.step(state);

	}

	@Override
	public String toString() {
		return "ASP" + id;
	}

	public void improveQuality(double amount) {
		// increase quality
		quality += amount;

		// account for financial impact of the investment
		double price = amount * s.qualityPrice;
		financials.invest(price);
	}

	public void customerUse(byte custID, double population, byte x, byte y,
			byte nspID) {
		// Send data to customer network
		// TODO: Scale by population size
		NetFlow flow = new NetFlow(this.id, datacenter,
				s.allNSPs[nspID].edgeNetworks[x][y], bandwidth * population);
		datacenter.originate(flow);

		// Record revenue
		double totalRevenue = population * this.price;
		this.financials.earnRevenue(totalRevenue);

	}

	public double getCustomers(int nspID) {
		double numCustomers = 0;
		for (Consumer c : s.allConsumers) {
			numCustomers += c.getASPNSPSubscribers((byte) nspID, (byte) id);
		}
		return numCustomers;
	}

	private void goBankrupt() {
		quality = 0;
		bankrupt = true;
	}

}
