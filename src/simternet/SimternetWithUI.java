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

	public Display2D display;

	public JFrame displayFrame;

	public ActiveCustomersPortrayal2D populationPortrayal = null;

	public SimternetWithUI() {
		super(new Simternet(System.currentTimeMillis()));
		this.populationPortrayal = new ActiveCustomersPortrayal2D(
				"Active Subscribers", (Simternet) this.state);
	}

	public SimternetWithUI(SimState state) {
		super(state);
		this.populationPortrayal = new ActiveCustomersPortrayal2D(
				"Active Subscribers", (Simternet) this.state);
	}

	@Override
	public void init(Controller c) {
		super.init(c);
		this.display = new Display2D(400, 400, this, 1);
		this.displayFrame = this.display.createFrame();
		c.registerFrame(this.displayFrame);
		this.displayFrame.setVisible(true);
		this.display.setBackdrop(Color.black);
		this.display.attach(this.populationPortrayal, "Active Subscribers");
	}

	@Override
	public void load(SimState state) {
		super.load(state);
		this.setupPortrayals();
	}

	@Override
	public void quit() {
		super.quit();

		if (this.displayFrame != null)
			this.displayFrame.dispose();
		// Allow garbage collection
		this.displayFrame = null;
		this.display = null;
	}

	public void setupPortrayals() {
		// tell the portrayals what to
		// portray and how to portray them
		this.populationPortrayal.setField(((Simternet) this.state)
				.getActiveSubscribersGrid());
		this.populationPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0,
				Exogenous.maxPopulation, Color.black, Color.white));

	}

	@Override
	public void start() {
		super.start();
		this.setupPortrayals(); // set up our portrayals
	}

}
