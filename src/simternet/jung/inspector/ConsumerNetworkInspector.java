package simternet.jung.inspector;

import simternet.jung.ConsumerNetwork;
import simternet.jung.gui.GUI;
import simternet.jung.inspector.property.DoubleProperty;
import simternet.jung.inspector.property.IntegerProperty;
import simternet.jung.inspector.property.StringProperty;

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

	protected StringProperty	location;
	protected ConsumerNetwork	net;
	protected IntegerProperty	numNSPs, population;
	protected DoubleProperty	percentage;
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
		super(network.toString(), owner);
		this.net = network;

		this.location = new StringProperty("Location");
		this.add(this.location);

		this.population = new IntegerProperty("Population");
		this.add(this.population);

		this.percentage = new DoubleProperty("Percentage Subscribing");
		this.add(this.percentage);

		this.numNSPs = new IntegerProperty("Connected NSPs");
		this.add(this.numNSPs);

		this.update();
	}

	/**
	 * Updates the displayed values with the current simulation values
	 */
	@Override
	public void update() {
		this.location.setValue(this.net.getLocation().toCoordinates());
		this.population.setValue(new Integer(this.net.getPopulation().intValue()));
		this.percentage.setValue(this.net.getPercentageSubscribing());
		this.numNSPs.setValue(this.net.getNumNetworkProviders());

	}
}
