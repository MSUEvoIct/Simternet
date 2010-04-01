package simternet.nsp;

import java.util.Set;

import javax.activation.UnsupportedDataTypeException;

import sim.engine.SimState;
import simternet.CournotSimternet;
import simternet.Exogenous;
import simternet.Simternet;
import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractNetwork;
import simternet.network.SimpleNetwork;

public class RepeatedStackelburgNSP extends AbstractNetworkProvider {

	private Boolean built = false;
	
	public RepeatedStackelburgNSP(Simternet s) {
		super(s);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void makeNetworkInvestment() {
		if (built == false)
			super.buildEverywhere(SimpleNetwork.class);
		built = true;
	}

	@Override
	protected void setPrices() {
		for (Object obj : networkGrid.allObjects.objs) { // for all our networks
			if (obj == null)
				continue;
			AbstractNetwork an = (AbstractNetwork) obj;
			if (an == null)
				continue;
			this.setPrice(an);
		}
	}
	
	private void setPrice(AbstractNetwork an) {
		Set<AbstractConsumerClass> consumerClasses = this.simternet.getConsumerClasses();
		for (AbstractConsumerClass cc : consumerClasses) {
			Double totPopAtLoc = cc.getPopulation(an.getLocationX(), an.getLocationY());
			Double othersQty = cc.getTotalLocalSubscriptions(an.getClass(), an.getLocationX(), an.getLocationY()) - an.getCustomers(cc);
			Double price = (Exogenous.maxPrice * (-1 * othersQty + totPopAtLoc)) / (2 * totPopAtLoc);
			an.setPrice(cc, price);
		}
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		super.step(state);
//		System.out.println("    Market Share = " + getTotalSubscribers() + " out of " + simternet.getPopulation());
//		System.out.println("    Market Share = " + ((CournotSimternet)simternet).getCurrentMarketSharePercentage(this));
		System.out.print("  Prices = ");
		for (Object obj : networkGrid.allObjects.objs) { // for all our networks
			if (obj == null)
				continue;
			AbstractNetwork an = (AbstractNetwork) obj;
			if (an == null)
				continue;
			System.out.print(an.getLocationX() + "x" + an.getLocationY() + "=" + an.getPrice(null).toString().substring(0, 4) + ", ");
		}
		System.out.print("\n");
		System.out.print("  Customers = ");
		for (Object obj : networkGrid.allObjects.objs) { // for all our networks
			if (obj == null)
				continue;
			AbstractNetwork an = (AbstractNetwork) obj;
			if (an == null)
				continue;
			System.out.print(an.getLocationX() + "x" + an.getLocationY() + "=" + an.getTotalCustomers() + ", ");
		}
		System.out.print("\n");
	}

	@Override
	public void updateData(SimState state) throws UnsupportedDataTypeException {
		System.out.println("Error! Sam added a new implementation in AbstractNetworProvider and didn't bother to update RepeatedStackelburg. How rude.");
		System.exit(0);
		
	}

}
