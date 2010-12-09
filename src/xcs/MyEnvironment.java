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
	public double executeAction(double action) {
		return action;
	}

	public int getAssets() {
		return this.assets;
	}

	@Override
	public int getConditionLength() {
		// return 10; //Because length of max int is 10
		return AgentData.ANTECEDENT_LENGTH;
	}

	public AgentData getCurrentState() {
		// return String.format("%010d", assets);
		// return String.valueOf(this.agentData.getDeltaRevenue());
		return this.agentData;
	}

	@Override
	public int getMaxPayoff() {
		// return assets = assets + Math.round(assets * 1 * ((float)10 / 50));
		return 1;
	}

	@Override
	public int getNrActions() {
		return 2;
	}

	@Override
	public boolean isMultiStepProblem() {
		return true;
	}

	@Override
	public AgentData resetState() {
		Random r = new Random(System.currentTimeMillis());
		this.agentData = new AgentData();
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
