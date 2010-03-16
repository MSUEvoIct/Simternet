package sim.app.mytutorial1and2;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import java.awt.*;
import javax.swing.*;


public class Tutorial2 extends GUIState{

	public Display2D display;
	public JFrame displayFrame;
	FastValueGridPortrayal2D gridPortrayal = new FastValueGridPortrayal2D();

	public Tutorial2(){
		super(new Tutorial1(System.currentTimeMillis()));
	}

	public Tutorial2(SimState state){
		super(state);
	}

	public void start(){
		super.start();
		setupPortrayals();
		display.reset();
		display.repaint();
	}

	public void load(SimState state){
		super.load(state);
		setupPortrayals();  // we now have new grids.  Set up the portrayals to reflect this
		display.reset();    // reschedule the displayer
		display.repaint();  // redraw the display
	}

	public void init(Controller c){
		super.init(c);

		// Make the Display2D.  We'll have it display stuff later.
		Tutorial1 tut = (Tutorial1)state;
		display = new Display2D(tut.gridWidth * 4, tut.gridHeight * 4,this,1);
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
		displayFrame.setVisible(true);

		display.attach(gridPortrayal,"Life");  // attach the portrayals

		// specify the backdrop color  -- what gets painted behind the displays
		display.setBackdrop(Color.black);
	}

	public static void main(String[] args){
		Tutorial2 tutorial2 = new Tutorial2();
		Console c = new Console(tutorial2);
		c.setVisible(true);
	}


	public static String getName(){
		return "Tutorial 2: Life";
	}

	public static Object getInfo(){
		return "<H2>Conway's game of life</H2>" +
		"<p>... with a B-Heptomino";
	}

	private void setupPortrayals(){
		//Tells the portrayals what to portray and how to portray them
		gridPortrayal.setField(((Tutorial1)state).grid);
		gridPortrayal.setMap(
				new sim.util.gui.SimpleColorMap(
						new Color [] {new Color (0,0,0,0), Color.blue}	
				)
		);
	}

}
