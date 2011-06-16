package simternet.jung.appearance;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.apache.commons.collections15.Transformer;

import simternet.Simternet;
import simternet.network.EdgeNetwork;

/**
 * Defines the shape and size of EdgeNetworks
 * 
 * Shape: Circle
 * 
 * Size: proportional to the consumer population at that location
 * 
 * @author graysonwright
 * 
 */
public class EdgeShapeTransformer implements Transformer<EdgeNetwork, Shape> {

	protected Simternet	s;

	protected double	scale	= .002;

	public EdgeShapeTransformer(Simternet s) {
		super();
		this.s = s;
	}

	public EdgeShapeTransformer(Simternet s, double scale) {
		super();
		this.s = s;
		this.scale = scale;
	}

	@Override
	public Shape transform(EdgeNetwork edge) {
		Double pop = this.s.getPopulation(edge.getLocation());
		Shape shape = new Ellipse2D.Double(-pop * this.scale / 2, -pop * this.scale / 2, pop * this.scale, pop
				* this.scale);

		return shape;

	}

}
