package xcs;

import java.io.Serializable;
import java.util.Random;

/*
 * Extremely simple environment where the agents can spend money, and the more money they spend
 * the more money they're rewarded with
 */
public class MyEnvironment implements Environment, Serializable {

	private AgentData agentData;
	private int assets = 1;
	private boolean correct = false;
	private boolean reset = false;

	@Override
	public boolean doReset() {
		return this.reset;
	}

	@Override
	public double executeAction(int action) {
		int sign = 1;
		switch (action) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			int lastDigit = this.assets % 10;
			// if(lastDigit == action){
			// sign = 1;
			// correct = true;
			// }else{
			// sign = -1;
			// correct = false;
			// }
			// if(assets > (2147483647/2)){
			if (this.assets % 2 == 0) {
				if (action > 4) {
					sign = 1;
					this.correct = true;
				} else {
					sign = -1;
					this.correct = false;
				}
			} else if (action <= 4) {
				sign = 1;
				this.correct = true;
			} else {
				sign = -1;
				this.correct = false;
			}
			this.assets = this.assets + sign;
			// assets = assets + Math.round(assets * sign * ((float)action /
			// 50));
			break;
		default:
			System.out
					.println("Fatal error, incorrect executeAction parameter "
							+ action);
			System.exit(-1);
			break;
		}
		Integer i = new Integer(sign);
		return i.doubleValue();
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
		return String.valueOf(this.agentData.getM_deltaRevenue());
	}

	@Override
	public int getMaxPayoff() {
		// return assets = assets + Math.round(assets * 1 * ((float)10 / 50));
		return 1;
	}

	@Override
	public int getNrActions() {
		return 10;
	}

	@Override
	public boolean isMultiStepProblem() {
		return true;
	}

	@Override
	public String resetState() {
		Random r = new Random(System.currentTimeMillis());
		this.assets = r.nextInt();
		return this.getCurrentState();
	}

	public void setAgentData(AgentData aD) {
		this.agentData = aD;
	}

	@Override
	public boolean wasCorrect() {
		return false;
	}

}
