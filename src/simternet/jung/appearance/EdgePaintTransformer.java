package simternet.jung.appearance;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import simternet.Simternet;
import simternet.network.EdgeNetwork;
import simternet.network.Network;

/**
 * Defines the color of Edge Networks
 * 
 * the color follows a gradient from red to green, representing the percentage
 * of consumer who are actively subscribing to a network provider at that time
 * 
 * @author graysonwright
 * 
 */
public class EdgePaintTransformer implements Transformer<Network, Paint> {

	protected Simternet	sim;

	public EdgePaintTransformer(Simternet sim) {
		super();
		this.sim = sim;
	}

	@Override
	public Paint transform(Network net) {
		if (net instanceof EdgeNetwork) {
			EdgeNetwork edge = (EdgeNetwork) net;
			double active = this.sim.getAllActiveSubscribersGrid().get(edge.getLocation().x, edge.getLocation().y);
			double pop = this.sim.getPopulation(edge.getLocation());
			double ratio = active / pop;

			Color edgePaint;
			if (ratio < 0)
				edgePaint = Color.BLACK;
			else if (ratio > 1)
				edgePaint = Color.BLACK;
			else
				edgePaint = new Color((float) (1 - ratio), (float) (ratio), 0);

			return edgePaint;

		} else
			return Color.RED;
	}

}
