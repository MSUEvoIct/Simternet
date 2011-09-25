package simternet.gui;

import java.awt.Graphics2D;
import java.io.Serializable;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import simternet.engine.Simternet;

public class ActiveCustomersPortrayal2D extends FastValueGridPortrayal2D
		implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
