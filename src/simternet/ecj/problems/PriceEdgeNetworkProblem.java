package simternet.ecj.problems;

import simternet.consumer.AbstractConsumerClass;
import simternet.network.AbstractEdgeNetwork;
import ec.Problem;

/**
 * This class contains the data structures that must be made available when
 * making the decision regarding how to price an edge network. However, the
 * individual pieces of data will not actually be used by the GP-generated
 * algorithm unless the corresponding GPNode terminals are listed as potential
 * functions in this tree.
 * 
 * @author kkoning
 * 
 */
public class PriceEdgeNetworkProblem extends Problem implements HasConsumerClass, HasEdgeNetwork {

	private AbstractConsumerClass	acc;
	private AbstractEdgeNetwork		aen;

	public PriceEdgeNetworkProblem(AbstractEdgeNetwork aen, AbstractConsumerClass acc) {
		this.aen = aen;
		this.acc = acc;
	}

	@Override
	public AbstractConsumerClass getConsumerClass() {
		return this.acc;
	}

	@Override
	public AbstractEdgeNetwork getEdgeNetwork() {
		return this.aen;
	}

}
