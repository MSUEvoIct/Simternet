package simternet.gui.jung.appearance;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import simternet.network.BackboneLink;

/**
 * LinkPaintTransformer
 * 
 * Works with JUNG's RenderContext to define the graphical representation of
 * Backbone Links. Each link is given a color dependent on its congestion ratio.
 * Line pattern and width is defined in a separate class, LinkStrokeTransformer
 * 
 * @author graysonwright
 */
public class LinkPaintTransformer implements Transformer<BackboneLink, Paint> {

	@Override
	public Paint transform(BackboneLink link) {

		double congestion = link.perStepCongestionRatio();

		// ideally, congestion shouldn't ever go out of this range.
		double min = 0;
		double max = 1.3;

		// create a gradient between GREEN and RED, min and max
		Color edgePaint;
		if (congestion <= min) {
			edgePaint = Color.GREEN;
		} else if (congestion >= max) {
			edgePaint = Color.RED;
		} else {
			double percent = (congestion - min) / (max - min);
			edgePaint = new Color((float) percent, (float) (1 - percent), 0);
		}

		return edgePaint;
	}
}
