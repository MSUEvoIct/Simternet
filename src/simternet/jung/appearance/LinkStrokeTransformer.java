package simternet.jung.appearance;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import simternet.network.BackboneLink;

/**
 * LinkStrokeTransformer
 * 
 * Works with JUNG's RenderContext to define the graphical representation of
 * Backbone Links. For now, all links are represented by a solid line, and the
 * width depends on the bandwidth of the link. Color is defined in a separate
 * class, LinkPaintTransformer
 * 
 * @author graysonwright
 */
public class LinkStrokeTransformer implements Transformer<BackboneLink, Stroke> {

	@Override
	public Stroke transform(BackboneLink link) {

		double bw = link.getBandwidth().doubleValue();

		float width = (float) (Math.log(bw));
		if (bw <= 0) {
			System.err.println("Illegal bandwidth: " + link.toString());
			float[] dash = { 5.0f, 5.0f };
			return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0);
		}
		if (width < 1)
			width = 1;
		return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
	}
}
