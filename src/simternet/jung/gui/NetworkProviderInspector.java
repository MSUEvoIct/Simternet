package simternet.jung.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;

import simternet.nsp.NetworkProvider;

public class NetworkProviderInspector extends Inspector {

	protected JLabel			locationLabel, ownerLabel, bandwidthLabel, priceLabel;
	protected static final int	numRows				= 5;

	private static final long	serialVersionUID	= 1L;

	public NetworkProviderInspector(NetworkProvider nsp, GUI owner) {
		super(nsp, owner);

		this.setLayout(new GridLayout(EdgeInspector.numRows, 2, 20, 5));
		//
		// this.add(new JLabel("Location"));
		// this.add(new JLabel(nsp.getLocation().toString()));
		//
		// this.add(new JLabel("Owner"));
		// this.add(new JLabel(nsp.getOwner().toString()));
		//
		// this.add(new JLabel("Max Bandwidth"));
		// this.add(new JLabel(nsp.getMaxBandwidth().toString()));
		//
		// this.add(new JLabel("Price"));
		// this.add(new JLabel(nsp.getPrice().toString()));

		this.add(new JLabel("0"));
		this.add(new JLabel("0"));
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
}
