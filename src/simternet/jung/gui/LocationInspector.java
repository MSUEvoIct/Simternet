package simternet.jung.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;

import sim.util.Int2D;
import simternet.Simternet;

/**
 * Inspects a location (Int2D) of a Simternet run
 * 
 * Currently displays the location, population, percentage of the population
 * subscribing to Internet, and the number of Network Providers who provide
 * service at the location.
 * 
 * @author graysonwright
 * 
 */
public class LocationInspector extends Inspector {

	protected JLabel			locationLabel, populationLabel, percentageLabel, nspLabel;
	protected static final int	numRows				= 4;
	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param location
	 *            the location to be inspected
	 * @param owner
	 *            the GUI in charge of this inspector
	 */
	public LocationInspector(Int2D location, GUI owner) {
		super(location, owner);

		this.setLayout(new GridLayout(EdgeInspector.numRows, 2, 20, 5));

		this.locationLabel = new JLabel();
		this.populationLabel = new JLabel();
		this.percentageLabel = new JLabel();
		this.nspLabel = new JLabel();

		this.add(new JLabel("Category"));
		this.add(this.locationLabel);

		this.add(new JLabel("Quality"));
		this.add(this.populationLabel);

		this.add(new JLabel("Subscription Price"));
		this.add(this.percentageLabel);

		this.add(new JLabel("Connected NSPs"));
		this.add(this.nspLabel);

		this.update();
	}

	/**
	 * Updates the displayed values with the current simulation values
	 */
	@Override
	public void update() {
		Simternet sim = this.owner.simternet;
		Int2D loc = (Int2D) this.object;

		this.locationLabel.setText(loc.toString());

		this.populationLabel.setText(sim.getPopulation(loc).toString());

		double percentage = sim.getPopulation(loc) / sim.getAllActiveSubscribersGrid().get(loc.x, loc.y);
		this.percentageLabel.setText(Double.toString(percentage));

		this.nspLabel.setText(sim.getNumNetworkProviders(loc).toString());

		// for(Network net : sim.getNetworks(null, null, loc)){
		// if(net instanceof EdgeNetwork){
		// EdgeNetwork edge = (EdgeNetwork) net;
		//
		// }
		// }
	}
}
