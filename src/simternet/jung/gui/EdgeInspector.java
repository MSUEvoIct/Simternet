package simternet.jung.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;

import simternet.network.EdgeNetwork;

public class EdgeInspector extends Inspector {

	protected JLabel			locationLabel, ownerLabel, bandwidthLabel, priceLabel;
	protected static final int	numRows				= 5;

	private static final long	serialVersionUID	= 1L;

	public EdgeInspector(EdgeNetwork net, GUI owner) {
		super(net, owner);

		this.setLayout(new GridLayout(EdgeInspector.numRows, 2, 20, 5));

		this.locationLabel = new JLabel();
		this.ownerLabel = new JLabel();
		this.bandwidthLabel = new JLabel();
		this.priceLabel = new JLabel();

		this.add(new JLabel("Location"));
		this.add(this.locationLabel);

		this.add(new JLabel("Owner"));
		this.add(this.ownerLabel);

		this.add(new JLabel("Max Bandwidth"));
		this.add(this.bandwidthLabel);

		this.add(new JLabel("Price"));
		this.add(this.priceLabel);

		this.add(new JLabel("0"));
		this.add(new JLabel("0"));

		this.update();
	}

	@Override
	public void update() {

		EdgeNetwork net = (EdgeNetwork) this.object;

		this.locationLabel.setText(net.getLocation().toString());
		this.ownerLabel.setText(net.getOwner().toString());
		this.bandwidthLabel.setText(net.getMaxBandwidth().toString());
		this.priceLabel.setText(net.getPrice().toString());
	}

}
