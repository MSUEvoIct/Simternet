package simternet.asp;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Financials;
import simternet.Simternet;
import simternet.consumer.Consumer;
import simternet.network.DataCenter;
import simternet.network.NetFlow;

public class ASP implements Steppable {
	private static final long serialVersionUID = 1L;

	public byte id;

	// Financial condition of this ASP
	public Financials financials = new Financials();

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
	DataCenter datacenter;

	/** Reference to the main simulation object */
	public Simternet s;

	/** The individual/agent controlling the ASP */
	public ASPIndividual ind;

	public ASP(Simternet s, ASPIndividual ind, byte aspID) {
		this.s = s;
		this.ind = ind;
		this.id = aspID;
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
		// Set quality
		ind.improveQuality(this, s);

		// Set price
		ind.setPrice(this, s);

		// Update bandwidth
		bandwidth = Math.pow(quality, s.qualityToBandwidthExponent);

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
				s.allNSPs[nspID].edgeNetworks[x][y], bandwidth);
		datacenter.originate(flow);

		// Record revenue
		double totalRevenue = population * this.price;
		this.financials.earnRevenue(totalRevenue);

	}

}