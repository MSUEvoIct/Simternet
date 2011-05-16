package simternet.jung;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import simternet.network.BackboneLink;

public class BackbonePaintTransformer implements Transformer<BackboneLink, Paint> {

	@Override
	public Paint transform(BackboneLink link) {

		double congestion = link.getCongestionAlgorithm().getCongestionRatio();
		System.out.println(link.toString() + "\tCongestion = " + congestion);

		double min = 0;
		double max = 1.3;

		Color edgePaint;
		if (congestion <= min)
			edgePaint = Color.GREEN;
		else if (congestion >= max)
			edgePaint = Color.RED;
		else {
			double percent = (congestion - min) / (max - min);
			edgePaint = new Color((float) percent, (float) (1 - percent), 0);
		}

		return edgePaint;
	}
}
