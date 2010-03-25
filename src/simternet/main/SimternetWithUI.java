package simternet.main;

import java.awt.Color;

import javax.swing.JFrame;

import sim.app.tutorial1and2.Tutorial1;
import sim.app.tutorial1and2.Tutorial2;
import sim.app.tutorial4.BigParticle;
import sim.app.tutorial4.BigParticleInspector;
import sim.app.tutorial4.Particle;
import sim.app.tutorial4.Tutorial4;
import sim.app.tutorial4.Tutorial4WithUI;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.LocationWrapper;

public class SimternetWithUI extends GUIState{
	
    public Display2D display;
    public JFrame displayFrame;

	public SimternetWithUI(){ super(new Simternet(System.currentTimeMillis())); }

	public SimternetWithUI(SimState state){ super(state); }

	public static void main(String[] args)
	{
		SimternetWithUI sim = new SimternetWithUI();
		Console c = new Console(sim);
		c.setVisible(true);
	}

	///////////////////

	public static String getName() { return "Simternet"; }

//	public void setupPortrayals()
//	{
//		// tell the portrayals what to portray and how to portray them
//		gridPortrayal.setField(((Tutorial1)state).grid);
//		gridPortrayal.setMap(
//				new sim.util.gui.SimpleColorMap(
//						new Color[] {new Color(0,0,0,0), Color.blue}));
//	}

	public void start()
	{
		super.start();      
//		setupPortrayals();  // set up our portrayals
		display.reset();    // reschedule the displayer
		display.repaint();  // redraw the display
	}

	public void init(Controller c)
	{
		super.init(c);

//		// Make the Display2D.  We'll have it display stuff later.
//		Tutorial1 tut = (Tutorial1)state;
//		display = new Display2D(tut.gridWidth * 4, tut.gridHeight * 4,this,1);
//		displayFrame = display.createFrame();
//		c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
//		displayFrame.setVisible(true);
//
//		// attach the portrayals
//		display.attach(gridPortrayal,"Life");
//
//		// specify the backdrop color  -- what gets painted behind the displays
//		display.setBackdrop(Color.black);
	}

	public void load(SimState state)
	{
		super.load(state);
//		setupPortrayals();  // we now have new grids.  Set up the portrayals to reflect this
		display.reset();    // reschedule the displayer
		display.repaint();  // redraw the display
	}

}
