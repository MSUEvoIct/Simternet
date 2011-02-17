package xcs;
import java.io.Serializable;


/**
 * This class generates a prediction array of the provided set.
 * The prediction array is generated according to Wilson's Classifier Fitness Based on Accuracy 
 * (Evolutionary Computation Journal, 1995).
 * Moreover, this class provides all methods to handle selection in the prediction array, essentially, to select the
 * best action, a present random action or an action by roulette wheel selection. 
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class PredictionArray implements Serializable
{
    /**
     * The prediction array.
     */
    private double[] pa;

    /**
     * The sum of the fitnesses of classifiers that represent each entry in the prediction array.
     */
    private double[] nr;
    
    /**
     * Constructs the prediction array according to the current set and the possible number of actions.
     *
     * @param set The classifier set out of which a prediction array is formed (normally the match set).
     * @param size The number of entries in the prediction array (should be set to the number of possible actions in the problem)
     */
    public PredictionArray(XClassifierSet set, int size)
    {
	pa= new double[size];
	nr= new double[size];
  
	for(int i=0; i<size; i++){
	    pa[i]=0.;
	    nr[i]=0.;
	}
	for(int i=0; i<set.getSize(); i++){
	    XClassifier cl= set.elementAt(i);
	    pa[cl.getAction()]+=(cl.getPrediction()*cl.getFitness());
	    nr[cl.getAction()]+=cl.getFitness();
	}
	for(int i=0; i<size; i++){
	    if(nr[i]!=0){
		pa[i]/=nr[i];
	    }else{
		pa[i]=0;
	    }
	}
    }

    /**
     * Returns the highest value in the prediction array.
     */
    public double getBestValue()
    {
	int i;
	double max;
	for(i=1, max=pa[0]; i<pa.length; i++){
	    if(max<pa[i])
		max=pa[i];
	}
	return max;
    }
    
    /**
     * Returns the value of the specified entry in the prediction array.
     */
    public double getValue(int i)
    {
	if(i>=0 && i<pa.length)
	    return pa[i];
	return -1.;
    }

    /*************** Action selection functions ****************/

    /**
     * Selects an action randomly. 
     * The function assures that the chosen action is represented by at least one classifier.
     */
    public int randomActionWinner()
    {
	int ret=0;
	do{
	    ret = (int)(XCSConstants.drand()*pa.length);
	}while(nr[ret]==0);
	return ret;
    }
    
    /**
     * Selects the action in the prediction array with the best value.
     */
    public int bestActionWinner()
    {
	int ret=0;
	for(int i=1; i<pa.length; i++){
	    if(pa[ret]<pa[i])
		ret=i;
	}
	return ret;
    }
    
    /**
     * Selects an action in the prediction array by roulette wheel selection.
     */
    public int rouletteActionWinner()
    {
	double bidSum=0.;
	int i;
	for(i=0; i<pa.length; i++)
	    bidSum+=pa[i];
	
	bidSum*=XCSConstants.drand();
	double bidC=0.;
	for(i=0; bidC<bidSum; i++){
	    bidC+=pa[i];
	}
	return i;
    }
}
