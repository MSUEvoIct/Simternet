package xcs;

import java.io.Serializable;
import java.util.Random;

/*
 * Extremely simple environment where the agents can spend money, and the more money they spend
 * the more money they're rewarded with
 */
public class MyEnvironment implements Environment, Serializable {

	private int		assets	= 1;
	private boolean	correct	= false;
	// private int targetAssets = 1000;
	private boolean	reset	= false;

	@Override
	public boolean doReset() {
		return this.reset;
	}

	@Override
	public double executeAction(int action) {
		switch (action) {
		case 0:
			return -1.0;
		case 1:
			return 0.0;
		case 2:
			return 1.0;
		}
		return 0.0;
	}

	public int getAssets() {
		return this.assets;
	}

	@Override
	public int getConditionLength() {
		// return 10; //Because length of max int is 10
		return 1;
	}

	@Override
	public String getCurrentState() {
		// return String.format("%010d", assets);
		return String.valueOf(this.assets);
	}

	@Override
	public int getMaxPayoff() {
		// return assets = assets + Math.round(assets * 1 * ((float)10 / 50));
		return 1;
	}

	@Override
	public int getNrActions() {
		return 3;
	}

	@Override
	public boolean isMultiStepProblem() {
		return false;
	}

	@Override
	public String resetState() {
		Random r = new Random(System.currentTimeMillis());
		this.assets = r.nextInt();
		return this.getCurrentState();
	}

	@Override
	public boolean wasCorrect() {
		return false;
	}

}
