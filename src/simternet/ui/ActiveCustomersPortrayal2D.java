package simternet.ui;

import java.awt.Graphics2D;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import simternet.Simternet;

public class ActiveCustomersPortrayal2D extends FastValueGridPortrayal2D {
	public Simternet simternet = null;

	public ActiveCustomersPortrayal2D(String string, Simternet s) {
		super(string);
		this.simternet = s;
	}

	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		this.setField(this.simternet.getAllActiveSubscribersGrid());
		super.draw(object, graphics, info);
	}
}
