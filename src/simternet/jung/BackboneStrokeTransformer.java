package simternet.jung;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import simternet.network.BackboneLink;

/**
 * BackboneStrokeTransformer
 * 
 * Works with JUNG's RenderContext to define the graphical representation of
 * Backbone Links. For now, all links are represented by a solid line, and the
 * width depends on the bandwidth of the link. Color is defined in a separate
 * class, BackbonePaintTransformer
 * 
 * @author graysonwright
 */
public class BackboneStrokeTransformer implements Transformer<BackboneLink, Stroke> {

	@Override
	public Stroke transform(BackboneLink link) {
		// TODO Auto-generated method stub

		double bw = link.getBandwidth();

		float width = (float) (Math.log10(bw) - 3);
		Stroke edgeStroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);

		return edgeStroke;
	}
}
