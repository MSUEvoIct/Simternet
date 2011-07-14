package simternet.jung.inspector;

import java.awt.GridLayout;

import javax.swing.JLabel;

import simternet.jung.ConsumerNetwork;
import simternet.jung.gui.GUI;

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
public class ConsumerNetworkInspector extends Inspector {

	protected JLabel			locationLabel, populationLabel, percentageLabel, nspLabel;
	protected static final int	numRows				= 4;
	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param network
	 *            the ConsumerNetwork to be inspected
	 * @param owner
	 *            the GUI in charge of this inspector
	 */
	public ConsumerNetworkInspector(ConsumerNetwork network, GUI owner) {
		super(network, owner);

		this.setLayout(new GridLayout(EdgeInspector.numRows, 2, 20, 5));

		this.locationLabel = new JLabel();
		this.populationLabel = new JLabel();
		this.percentageLabel = new JLabel();
		this.nspLabel = new JLabel();

		this.add(new JLabel("Category"));
		this.add(this.locationLabel);

		this.add(new JLabel("Population"));
		this.add(this.populationLabel);

		this.add(new JLabel("Percentage Subscribing"));
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
		ConsumerNetwork net = (ConsumerNetwork) this.object;

		this.locationLabel.setText(net.toString());

		this.populationLabel.setText(net.getPopulation().toString());

		double percentage = net.getActiveSubscribers() / net.getPopulation();
		this.percentageLabel.setText(Double.toString(percentage));

		this.nspLabel.setText(net.getNumNetworkProviders().toString());

	}
}
