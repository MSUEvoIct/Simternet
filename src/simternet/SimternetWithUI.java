package simternet;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import simternet.ui.ActiveCustomersPortrayal2D;

public class SimternetWithUI extends GUIState {

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

	public JFrame displayFrameOverallActiveCustomers;

	public Display2D displayOverallActiveCustomers;

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

	@Override
	public void init(Controller c) {
		super.init(c);
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
	}

	@Override
	public void start() {
		super.start();
		this.setupPortrayals(); // set up our portrayals
	}

}
