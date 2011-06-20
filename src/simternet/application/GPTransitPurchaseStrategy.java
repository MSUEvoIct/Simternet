package simternet.application;

import java.io.Serializable;

import simternet.ecj.DoubleGP;
import simternet.ecj.problems.ASPPurchaseTransitProblem;
import simternet.network.BackboneLink;
import simternet.nsp.NetworkProvider;
import ec.gp.GPIndividual;
import ec.gp.GPTree;

public class GPTransitPurchaseStrategy implements TransitPurchaseStrategy, Serializable {

    // prevent infinities
	public static final Double			MAX_AMOUNT	= 1E50; 

	// can't purchase negative bandwidth;
	public static final Double			MIN_AMOUNT	= 0.0;


	protected final ApplicationProvider	asp;
	protected final GPIndividual		ind;
	protected final GPTree				tree;

	public GPTransitPurchaseStrategy(ApplicationProvider asp, GPIndividual ind, GPTree tree) {
		this.asp = asp;
		this.ind = ind;
		this.tree = tree;
	}

	@Override
	public Double bandwidthToPurchase(NetworkProvider destination, Double price) {
		DoubleGP d = new DoubleGP();
		d.value = 0.0;

		BackboneLink existing = this.asp.getDataCenter().getEgressLink(destination.getBackboneNetwork());

		ASPPurchaseTransitProblem aptp = new ASPPurchaseTransitProblem(this.asp, destination, existing);
		this.tree.child.eval(null, 0, d, null, this.ind, aptp);

		if (d.value > GPTransitPurchaseStrategy.MAX_AMOUNT)
			d.value = GPTransitPurchaseStrategy.MAX_AMOUNT;

		if (d.value < GPTransitPurchaseStrategy.MIN_AMOUNT)
			d.value = GPTransitPurchaseStrategy.MIN_AMOUNT;

		return d.value;
	}

}
