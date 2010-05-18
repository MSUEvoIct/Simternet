package simternet;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import simternet.ui.ActiveCustomersPortrayal2D;
import simternet.ui.NetworkProvidersDisplay;

public class SimternetWithUI extends GUIState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Object getInfo() {
		return "<H2>Simternet</H2><p>Simulates the growth of the Internet.";
	}

	public static String getName() {
		return "Simternet";
	}

	public static void main(String[] args) {
		SimternetWithUI sim = new SimternetWithUI();
		Console c = new Console(sim);
		c.setVisible(true);
	}

	/**
	 * This is a reference to the simulation controller. (This controls the
	 * window which is used to stop/start the simulation etc. We retain a
	 * reference outside the scope of the init() function because we will be
	 * modifying the display programatically based on the simulation. (i.e.,
	 * adding displays for new network service providers)
	 */
	private Controller controller;

	public JFrame displayFrameOverallActiveCustomers;

	public Display2D displayOverallActiveCustomers;
	protected NetworkProvidersDisplay netProvidersDisplay;

	public ActiveCustomersPortrayal2D overallActiveCustomersPortrayal = null;
	public Simternet s = null;

	public SimternetWithUI() {
		this(new Simternet(System.currentTimeMillis()));
	}

	public SimternetWithUI(SimState state) {
		super(state);
		this.s = (Simternet) state;
		this.overallActiveCustomersPortrayal = new ActiveCustomersPortrayal2D(
				"All Active Subscribers", (Simternet) this.state);
	}

	public void addDisplay(Display2D display) {
		JFrame frame = display.createFrame();
		frame.setTitle(display.getName());
		frame.setVisible(false);
		this.controller.registerFrame(frame);
	}

	@Override
	public void init(Controller c) {
		super.init(c);
		this.controller = c;
		this.displayOverallActiveCustomers = new Display2D(400, 400, this, 1);
		this.displayFrameOverallActiveCustomers = this.displayOverallActiveCustomers
				.createFrame();
		c.registerFrame(this.displayFrameOverallActiveCustomers);
		this.displayFrameOverallActiveCustomers.setVisible(true);
		this.displayOverallActiveCustomers.setBackdrop(Color.black);
		this.displayOverallActiveCustomers.attach(
				this.overallActiveCustomersPortrayal, "All Active Subscribers");
	}

	@Override
	public void load(SimState state) {
		super.load(state);
		this.setupPortrayals();
	}

	@Override
	public void quit() {
		super.quit();

		if (this.displayFrameOverallActiveCustomers != null)
			this.displayFrameOverallActiveCustomers.dispose();
		// Allow garbage collection
		this.displayFrameOverallActiveCustomers = null;
		this.displayOverallActiveCustomers = null;
	}

	public void setupPortrayals() {
		// tell the portrayals what to
		// portray and how to portray them
		this.overallActiveCustomersPortrayal
				.setMap(new sim.util.gui.SimpleColorMap(0.0,
						Exogenous.maxPopulation, Color.black, Color.white));

		// reschedule the displayer
		this.displayOverallActiveCustomers.reset();
		// redraw the display
		this.displayOverallActiveCustomers.repaint();

		//Network Providers Display
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
