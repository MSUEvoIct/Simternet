package simternet.agents.asp.strategies;

import java.io.Serializable;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.asp.TransitPurchaseStrategy;
import simternet.agents.nsp.NetworkProvider;
import simternet.ecj.DoubleGP;
import simternet.ecj.problems.ASPPurchaseTransitProblem;
import simternet.network.BackboneLink;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPTransitPurchaseStrategy implements TransitPurchaseStrategy, Serializable {

	protected final ApplicationProvider	asp;

	protected final GPIndividual		ind;

	protected final GPTree				tree;

	// prevent infinities
	public static final Double			MAX_AMOUNT	= 1E14;
	// can't purchase negative bandwidth;
	public static final Double			MIN_AMOUNT	= 0.0;

	// TODO: uncomment this. Commented for debugging.
	// private static final long serialVersionUID = 1L;

	public GPTransitPurchaseStrategy(ApplicationProvider asp, GPIndividual ind, GPTree tree) {
		this.asp = asp;
		this.ind = ind;
		this.tree = tree;
	}

	@Override
	public Double bandwidthToPurchase(NetworkProvider destination, Double price) {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;

		BackboneLink existing = asp.getDatacenter().getEgressLink(destination.getBackboneNetwork());

		ASPPurchaseTransitProblem aptp = new ASPPurchaseTransitProblem(asp, destination, existing);
		tree.child.eval(null, 0, d, null, ind, aptp);

		if (d.value > GPTransitPurchaseStrategy.MAX_AMOUNT) {
			d.value = GPTransitPurchaseStrategy.MAX_AMOUNT;
		}

		if (d.value < GPTransitPurchaseStrategy.MIN_AMOUNT) {
			d.value = GPTransitPurchaseStrategy.MIN_AMOUNT;
		}

		if (new Double(d.value).isNaN())
			// Printing out the full exception takes up too much console space.
			// System.err
			// .println("Exception at simternet.application.GPTransitPurchaseStrategy(52): Purchase amount is NaN");
			return 0.0;

		return d.value;
	}

}
