package xcs;
import java.io.Serializable;
import java.util.Random;

/*
 * Extremely simple environment where the agents can spend money, and the more money they spend
 * the more money they're rewarded with
 */
public class MyEnvironment implements Environment, Serializable{

	private int assets = 1;
	//private int targetAssets = 1000;
	private boolean reset = false;
	private boolean correct = false;
	
	@Override
	public boolean doReset() {
		return reset;
	}

	@Override
	public double executeAction(int action) {
		int sign = 1;
		switch(action){
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
			int lastDigit = assets % 10;
//			if(lastDigit == action){
//				sign = 1;
//				correct = true;
//			}else{
//				sign = -1;
//				correct = false;
//			}
			//if(assets > (2147483647/2)){
			if(assets % 2 == 0){	
				if(action > 4){
					sign = 1;
					correct = true;
				}else{
					sign = -1;
					correct = false;
				}
			}else{
				if(action <= 4){
					sign = 1;
					correct = true;
				}else{
					sign = -1;
					correct = false;
				}
			}
			assets = assets + sign;
			//assets = assets + Math.round(assets * sign * ((float)action / 50));
			break;
		default:
			System.out.println("Fatal error, incorrect executeAction parameter " + action);
			System.exit(-1);
			break;
		}
		Integer i = new Integer(sign);
		return i.doubleValue();
	}

	@Override
	public int getConditionLength() {
		//return 10; //Because length of max int is 10
		return 1;
	}

	@Override
	public String getCurrentState() {
		//return String.format("%010d", assets);
		return String.valueOf(assets);
	}

	@Override
	public int getMaxPayoff() {
		//return assets = assets + Math.round(assets * 1 * ((float)10 / 50));
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
		assets = r.nextInt();
		return getCurrentState();
	}

	@Override
	public boolean wasCorrect() {
		return false;
	}
	
	public int getAssets() {
		return assets;
	}

}
