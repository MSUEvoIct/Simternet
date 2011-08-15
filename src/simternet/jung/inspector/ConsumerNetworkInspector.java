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
		net = network;

		location = new StringProperty("Location", sim);
		this.add(location);

		population = new IntegerProperty("Population", sim);
		this.add(population);

		percentage = new DoubleProperty("Percentage Subscribing", sim);
		this.add(percentage);

		numNSPs = new IntegerProperty("Connected NSPs", sim);
		this.add(numNSPs);

		this.update();
	}

	/**
	 * Updates the displayed values with the current simulation values
	 */
	@Override
	public void update() {
		location.setValue(net.getLocation().toCoordinates());
		population.setValue(new Integer(net.getPopulation().intValue()));
		percentage.setValue(net.getPercentageSubscribing());
		numNSPs.setValue(net.getNumNetworkProviders());

	}
}
