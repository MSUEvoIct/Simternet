package simternet;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import simternet.ui.ActiveCustomersPortrayal2D;
import simternet.ui.NetworkProvidersDisplay;

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

	public JFrame displayFrameOverallActiveCustomers;

	public Display2D displayOverallActiveCustomers;
	protected NetworkProvidersDisplay netProvidersDisplay;

	public ActiveCustomersPortrayal2D overallActiveCustomersPortrayal = null;

	public SimternetWithUI() {
		this(new Simternet(System.currentTimeMillis(), true));
	}

	public SimternetWithUI(SimState state) {
		super(state);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();

		this.controller.unregisterAllFrames();

		if (this.displayFrameOverallActiveCustomers != null)
			this.displayFrameOverallActiveCustomers.dispose();

		if (this.netProvidersDisplay != null)
			this.netProvidersDisplay.dispose();

		// Allow garbage collection
		this.displayFrameOverallActiveCustomers = null;
		this.displayOverallActiveCustomers = null;
		this.netProvidersDisplay = null;

	}

	@Override
	public Inspector getInspector() {
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
	}

	@Override
	public Object getSimulationInspectedObject() {
		return ((Simternet) this.state).sio;
	} // non-volatile

	@Override
	public void init(Controller c) {
		super.init(c);
	}

	@Override
	public void load(SimState state) {
		super.load(state);
		this.setupPortrayals();
	}

	@Override
	public void quit() {
		super.quit();

	}

	public void setupPortrayals() {

		this.overallActiveCustomersPortrayal = new ActiveCustomersPortrayal2D(
				"All Active Subscribers", (Simternet) this.state);

		this.displayOverallActiveCustomers = new Display2D(400, 400, this, 1);
		this.displayFrameOverallActiveCustomers = this.displayOverallActiveCustomers
				.createFrame();

		this.controller.registerFrame(this.displayFrameOverallActiveCustomers);
		this.displayFrameOverallActiveCustomers.setVisible(true);
		this.displayOverallActiveCustomers.setBackdrop(Color.black);
		this.displayOverallActiveCustomers.attach(
				this.overallActiveCustomersPortrayal, "All Active Subscribers");

		// tell the portrayals what to
		// portray and how to portray them
		this.overallActiveCustomersPortrayal
				.setMap(new sim.util.gui.SimpleColorMap(0.0, Integer
						.parseInt(((Simternet) this.state).parameters
								.getProperty("landscape.population.max")),
						Color.black, Color.white));

		// reschedule the displayer
		this.displayOverallActiveCustomers.reset();
		// redraw the display
		this.displayOverallActiveCustomers.repaint();

		// Network Providers Display
		this.netProvidersDisplay = new NetworkProvidersDisplay(400, 400, this,
				1);
		this.controller.registerFrame(this.netProvidersDisplay);

	}

	@Override
	public void start() {
		super.start();
		this.setupPortrayals(); // set up our portrayals
	}

}
