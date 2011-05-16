package simternet.jung;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import simternet.network.BackboneLink;

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
