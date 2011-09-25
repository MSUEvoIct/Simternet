package simternet.engine;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import simternet.gui.ActiveCustomersPortrayal2D;
import simternet.gui.NetworkProvidersDisplay;

public class SimternetWithUI extends GUIState {

	public static Object getInfo() {
		return "<H2>Simternet</H2><p>Simulates the growth of the Internet.";
	}

	public static String getName() {
		return "Simternet";
	}

	/**
	 * Start the Simternet GUI program.
	 * 
	 * @param args
	 *            None accepted at this time. Further, this is the function
	 *            called to start the GUI within which a simulation should run.
	 *            Input parameters to the simulation itself should be made
	 *            elsewhere.
	 */
	public static void main(String[] args) {
		SimternetWithUI sim = new SimternetWithUI();
		Console c = new Console(sim);
		c.setVisible(true);
	}

	public JFrame						displayFrameOverallActiveCustomers;

	public Display2D					displayOverallActiveCustomers;
	protected NetworkProvidersDisplay	netProvidersDisplay;

	public ActiveCustomersPortrayal2D	overallActiveCustomersPortrayal	= null;

	public Simternet					s;

	public SimternetWithUI() {
		this(new Simternet(System.currentTimeMillis()));
	}

	public SimternetWithUI(SimState state) {
		super(state);
		s = (Simternet) state;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();

		controller.unregisterAllFrames();

		if (displayFrameOverallActiveCustomers != null) {
			displayFrameOverallActiveCustomers.dispose();
		}

		if (netProvidersDisplay != null) {
			netProvidersDisplay.dispose();
		}

		// Allow garbage collection
		displayFrameOverallActiveCustomers = null;
		displayOverallActiveCustomers = null;
		netProvidersDisplay = null;

	}

	// @Override
	// public Inspector getInspector() {
	// Inspector i = super.getInspector();
	// i.setVolatile(true);
	// return i;
	// }

	// @Override
	// public Object getSimulationInspectedObject() {
	// // TODO: Return custom parameters object
	// return new ParametersInspectionObject(((Simternet) this.state)
	// .getParameters());
	// }

	@Override
	public void init(Controller c) {
		super.init(c);
	}

	@Override
	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	@Override
	public void quit() {
		super.quit();

	}

	public void setupPortrayals() {

		overallActiveCustomersPortrayal = new ActiveCustomersPortrayal2D("All Active Subscribers", (Simternet) state);

		displayOverallActiveCustomers = new Display2D(400, 400, this, 1);
		displayFrameOverallActiveCustomers = displayOverallActiveCustomers.createFrame();

		controller.registerFrame(displayFrameOverallActiveCustomers);
		displayFrameOverallActiveCustomers.setVisible(true);
		displayOverallActiveCustomers.setBackdrop(Color.black);
		displayOverallActiveCustomers.attach(overallActiveCustomersPortrayal, "All Active Subscribers");

		// tell the portrayals what to
		// portray and how to portray them
		overallActiveCustomersPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, s.config.consumerPopulationMax,
				Color.black, Color.white));

		// reschedule the displayer
		displayOverallActiveCustomers.reset();
		// redraw the display
		displayOverallActiveCustomers.repaint();

		// Network Providers Display
		netProvidersDisplay = new NetworkProvidersDisplay(400, 400, this, 1);
		controller.registerFrame(netProvidersDisplay);

	}

	@Override
	public void start() {
		super.start();
		setupPortrayals(); // set up our portrayals
	}

}
