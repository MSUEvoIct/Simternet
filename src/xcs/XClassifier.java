package xcs;
import java.io.Serializable;
import java.io.PrintWriter;

/**
 * Each instance of this class represents one classifier. The class provides different constructors for generating
 * <ul>
 * <li> copies of existing classifiers,
 * <li> new matching classifiers with random action,
 * <li> new matching classifiers with specified action, and
 * <li> new completely random classifier.
 * </ul>
 * It handles classifier mutation and crossover and provides, sets, and updates parameters. 
 * Moreover, it handles all types of comparisons between different classifiers.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class XClassifier implements Serializable
{

    /**
     * The condition of this classifier.
     */
    private String condition;

    /**
     * The action of this classifier.
     */
    private int action;
  

    /**
     * The reward prediction value of this classifier.
     */
    private double prediction;

    /**
     * The reward prediction error of this classifier. 
     */
    private double predictionError;

    /**
     * The fitness of the classifier in terms of the macro-classifier.
     */
    private double fitness;
  

    /**
     * The numerosity of the classifier. This is the number of micro-classifier this macro-classifier represents.
     */
    private int numerosity;

    /**
     * The experience of the classifier. This is the number of problems the classifier learned from so far.
     */
    private int experience;

    /**
     * The action set size estimate of the classifier.
     */
    private double actionSetSize;

    /**
     * The time the last GA application took place in this classifier.
     */
    private int timeStamp;
    
    /**
     * An instance of the learning parameters in XCSJava. 
     * Static assures that the Constants are not generated for each classifier separately.
     */
    private static XCSConstants cons=new XCSConstants();




    /**
     * Constructs a classifier with matching condition and specified action.
     *
     * @param setSize The size of the current set which the new classifier matches.
     * @param time  The actual number of instances the XCS learned from so far.
     * @param situation The current problem instance/perception.
     * @param act The action of the new classifier.
     */
    public XClassifier(double setSize, int time, String situation, int act)
    {			
	createMatchingCondition(situation);
	action=act;
	classifierSetVariables(setSize, time);
    }
  
    /**
     * Construct matching classifier with random action.
     *
     * @param setSize The size of the current set which the new classifier matches.
     * @param time The actual number of instances the XCS learned from so far.
     * @param numberOfActions The number of different actions to chose from 
     * (This should be set to the number of actions possible in the problem).
     * @param situation The current problem instance/perception.
     */
    public XClassifier(double setSize, int time, int numberOfActions, String situation)
    {
	createMatchingCondition(situation);
	createRandomAction(numberOfActions);
	classifierSetVariables(setSize, time);
    }
	
    /**
     * Construct a classifier with random condition and random action.
     *
     * @param setSize The size of the current set which the new classifier matches.
     * @param time  The actual number of instances the XCS learned from so far.
     * @param condLength The length of the condition of the new classifier.
     * @param numberOfActions The number of different actions to chose from 
     */ 
    public XClassifier(double setSize, int  time, int condLength, int numberOfActions)
    {
	createRandomCondition(condLength);
	createRandomAction(numberOfActions);
	classifierSetVariables(setSize, time);
    }
  
    /**
     * Constructs an identical XClassifier.
     * However, the experience of the copy is set to 0 and the numerosity is set to 1 since this is indeed 
     * a new individual in a population.
     *
     * @param clOld The to be copied classifier.
     */
    public XClassifier(XClassifier clOld)
    {
	condition=new String(clOld.condition);
	action=clOld.action;	
	this.prediction=clOld.prediction;
	this.predictionError=clOld.predictionError;
	// Here we should divide the fitness by the numerosity to get a accurate value for the new one!
	this.fitness=clOld.fitness/clOld.numerosity;
	this.numerosity=1;
	this.experience=0;
	this.actionSetSize=clOld.actionSetSize;
	this.timeStamp=clOld.timeStamp;
    }
  
    /**
     * Creates a condition randomly considering the constant <code>P_dontcare<\code>.
     *
     * @see XCSConstants#P_dontcare
     */
    private void createRandomCondition(int condLength)
    {
	char condArray[]=new char[condLength];
	for(int i=0; i<condLength; i++){
	    if(cons.drand()<cons.P_dontcare)
		condArray[i]=cons.dontCare;
	    else
		if(cons.drand()<0.5)
		    condArray[i]='0';
		else
		    condArray[i]='1';
	}
	condition=new String(condArray);
    }	
		
    /**
     * Creates a matching condition considering the constant <code>P_dontcare<\code>.
     *
     * @see XCSConstants#P_dontcare
     */
    private void createMatchingCondition(String cond)
    {
	int condLength=cond.length();
	char condArray[]= new char[condLength];
    
	for(int i=0; i<condLength; i++){
	    if(cons.drand()<cons.P_dontcare)
		condArray[i]=cons.dontCare;
	    else
		condArray[i]=cond.charAt(i);
	}
	condition=new String(condArray);
    }
  
    /**
     * Creates a random action.
     *
     * @param numberOfActions The number of actions to chose from.
     */
    private void createRandomAction(int numberOfActions)
    {
	action=(int)(cons.drand()*numberOfActions);
    }		
  
    /** 
     * Sets the initial variables of a new classifier.
     *
     * @see XCSConstants#predictionIni
     * @see XCSConstants#predictionErrorIni
     * @see XCSConstants#fitnessIni
     * @param setSize The size of the set the classifier is created in.
     * @param time The actual number of instances the XCS learned from so far.
     */
    private void classifierSetVariables(double setSize, int time)
    {		
	this.prediction=cons.predictionIni;
	this.predictionError=cons.predictionErrorIni;
	this.fitness=cons.fitnessIni;
    
	this.numerosity=1;
	this.experience=0;
	this.actionSetSize=setSize;
	this.timeStamp=time;
    }

    /**
     * Returns if the classifier matches in the current situation.
     *
     * @param state The current situation which can be the current state or problem instance.
     */
    public boolean match(String state)
    {
	if(condition.length()!=state.length())
	    return false;
	for(int i=0; i<condition.length(); i++){
	    if(condition.charAt(i)!=cons.dontCare && condition.charAt(i)!=state.charAt(i))
		return false;
	}
	return true;
    }
	
    /**
     * Applies two point crossover and returns if the classifiers changed.
     *
     * @see XCSConstants#pX
     * @param cl The second classifier for the crossover application.
     */
    public boolean twoPointCrossover(XClassifier cl)
    {
	boolean changed=false;
	if(cons.drand()<cons.pX){
	    int length=condition.length();
	    int sep1=(int)(cons.drand()*(length));
	    int sep2=(int)(cons.drand()*(length))+1;
	    if(sep1>sep2){
		int help=sep1;
		sep1=sep2;
		sep2=help;
	    }else if(sep1==sep2){
		sep2++;
	    }
	    char[] cond1=condition.toCharArray();
	    char[] cond2=cl.condition.toCharArray();
	    for(int i=sep1; i<sep2; i++){
		if(cond1[i]!=cond2[i]){
		    changed=true;
		    char help=cond1[i];
		    cond1[i]=cond2[i];
		    cond2[i]=help;
		}
	    }
	    if(changed){
		condition=new String(cond1);
		cl.condition=new String(cond2);
	    }
	}
	return changed;
    }
  
    /**
     * Applies a niche mutation to the classifier. 
     * This method calls mutateCondition(state) and mutateAction(numberOfActions) and returns 
     * if at least one bit or the action was mutated.
     *
     * @param state The current situation/problem instance
     * @param numberOfActions The maximal number of actions possible in the environment.
     */
    public boolean applyMutation(String state, int numberOfActions)
    {
	boolean changed=mutateCondition(state);
	if(mutateAction(numberOfActions))
	    changed=true;
	return changed;			
    }

    /**
     * Mutates the condition of the classifier. If one allele is mutated depends on the constant pM. 
     * This mutation is a niche mutation. It assures that the resulting classifier
     * still matches the current situation.
     *
     * @see XCSConstants#pM
     * @param state The current situation/problem instance.
     */
    private boolean mutateCondition(String state)
    {
	boolean changed=false;
	int condLength=condition.length();
    
	for(int i=0; i<condLength; i++){
	    if(cons.drand()<cons.pM){
		char[] cond=condition.toCharArray();
		char[] stateC=state.toCharArray();
		changed=true;
		if(cond[i]==cons.dontCare){
		    cond[i]=stateC[i];
		}else{
		    cond[i]=cons.dontCare;
		}
		condition=new String(cond);
	    }
	}
	return changed;
    }
	
    /**
     * Mutates the action of the classifier.
     *
     * @see XCSConstants#pM
     * @param numberOfActions The number of actions/classifications possible in the environment.
     */
    private boolean mutateAction(int numberOfActions)
    {
	boolean changed=false;
    
	if(cons.drand()<cons.pM){
	    int act=0;
	    do{
		act=(int)(cons.drand()*numberOfActions);
	    }while(act==action);
	    action=act;
	    changed=true;
	}
	return changed;
    }
  
    /**
     * Returns if the two classifiers are identical in condition and action.
     *
     * @param cl The classifier to be compared.
     */
    public boolean equals(XClassifier cl)
    {
	if(cl.condition.equals( condition ))
	    if(cl.action ==  action)
		return true;
	return false;
    }
  
    /**
     * Returns if the classifier subsumes cl.
     *
     * @param The new classifier that possibly is subsumed. 
     */
    public boolean subsumes(XClassifier cl)
    {
	if(cl.action == action)
	    if(isSubsumer())
		if(isMoreGeneral(cl))
		    return true;
	return false;
    }

    /**
     * Returns if the classifier is a possible subsumer. It is affirmed if the classifier
     * has a sufficient experience and if its reward prediction error is sufficiently low.
     *
     * @see XCSConstants#theta_sub
     * @see XCSConstants#epsilon_0
     */
    public boolean isSubsumer()
    {
	if(experience>cons.theta_sub && predictionError < (double)cons.epsilon_0)
	    return true;
	return false;
    }
 
    /**
     * Returns if the classifier is more general than cl. It is made sure that the classifier is indeed more general and 
     * not equally general as well as that the more specific classifier is completely included in the more general one 
     * (do not specify overlapping regions)
     *
     * @param The classifier that is tested to be more specific.
     */
    public boolean isMoreGeneral(XClassifier cl)
    {
	boolean ret=false;
	int length=condition.length();
	for(int i=0; i<length; i++){
	    if(condition.charAt(i) != cons.dontCare && condition.charAt(i) != cl.condition.charAt(i))
		return false;
	    else if(condition.charAt(i) !=  cl.condition.charAt(i))
		ret = true;
	}
	return ret;
    }

    /**
     * Returns the vote for deletion of the classifier.
     *
     * @see XCSConstants#delta
     * @see XCSConstants#theta_del
     * @param meanFitness The mean fitness in the population.
     */
    public double getDelProp(double meanFitness)
    {
	if(fitness/numerosity >= cons.delta*meanFitness || experience < cons.theta_del)
	    return actionSetSize*numerosity;
	return actionSetSize*numerosity*meanFitness / ( fitness/numerosity);
    }

    /**
     * Updates the prediction of the classifier according to P.
     *
     * @see XCSConstants#beta
     * @param P The actual Q-payoff value (actual reward + max of predicted reward in the following situation).
     */
    public double updatePrediction(double P)
    {
	if( (double)experience < 1./cons.beta){
	    prediction = (prediction * ((double)experience - 1.) + P) / (double)experience;
	}else{
	    prediction += cons.beta * (P-prediction);
	}
	return prediction*numerosity;
    }
  
    /**
     * Updates the prediction error of the classifier according to P.
     *
     * @see XCSConstants#beta
     * @param P The actual Q-payoff value (actual reward + max of predicted reward in the following situation).
     */
    public double updatePreError(double P)
    {
	if( (double)experience < 1./cons.beta){
	    predictionError = (predictionError*((double)experience - 1.) + Math.abs(P - prediction)) / (double)experience;
	}else{
	    predictionError += cons.beta * (Math.abs(P - prediction) - predictionError);
	}
	return predictionError*numerosity;
    }
  
    /**
     * Returns the accuracy of the classifier.
     * The accuracy is determined from the prediction error of the classifier using Wilson's 
     * power function as published in 'Get Real! XCS with continuous-valued inputs' (1999)
     *
     * @see XCSConstants#epsilon_0
     * @see XCSConstants#alpha
     * @see XCSConstants#nu
     */
    public double getAccuracy()
    {
	double accuracy;

	if(predictionError <= (double)cons.epsilon_0){
	    accuracy = 1.;
	}else{
	    accuracy = cons.alpha * Math.pow( predictionError / cons.epsilon_0 , -cons.nu);
	}
	return accuracy;
    }

    /**
     * Updates the fitness of the classifier according to the relative accuracy.
     *
     * @see XCSConstants#beta
     * @param accSum The sum of all the accuracies in the action set
     * @param accuracy The accuracy of the classifier.
     */
    public double updateFitness(double accSum, double accuracy)
    {
	fitness += cons.beta * ((accuracy * numerosity) / accSum - fitness);	
	return fitness;//fitness already considers numerosity
    }

    /**
     * Updates the action set size.
     *
     * @see XCSConstants#beta
     * @param numeriositySum The number of micro-classifiers in the population
     */
    public double updateActionSetSize(double numerositySum)
    {
	if(experience < 1./cons.beta){
	    actionSetSize= (actionSetSize * (double)(experience-1)+ numerositySum) / (double)experience;
	}else{
	    actionSetSize+= cons.beta * (numerositySum - actionSetSize);
	}
	return actionSetSize*numerosity;
    }

    /**
     * Returns the action of the classifier.
     */
    public int getAction()
    {
	return action;
    }

    /**
     * Increases the Experience of the classifier by one.
     */
    public void increaseExperience()
    {
	experience++;
    }
  
    /**
     * Returns the prediction of the classifier.
     */
    public double getPrediction()
    {
	return prediction;
    }
    
    /**
     * Sets the prediction of the classifier.
     *
     * @param pre The new prediction of the classifier.
     */
    public void setPrediction(double pre)
    {
	prediction=pre;
    }
    
    
    /**
     * Returns the prediction error of the classifier.
     */
    public double getPredictionError()
    {
	return predictionError;
    }
    
    /**
     * Sets the prediction error of the classifier.
     *
     * @param preE The new prediction error of the classifier.
     */
    public void setPredictionError(double preE)
    {
	predictionError=preE;
    }

    /**
     * Returns the fitness of the classifier.
     */
    public double getFitness()
    {
	return fitness;
    }
    
    /**
     * Sets the fitness of the classifier.
     *
     * @param fit The new fitness of the classifier.
     */
    public void setFitness(double fit)
    {
	fitness=fit;
    }
    
    /**
     * Returns the numerosity of the classifier.
     */
    public int getNumerosity()
    {
	return numerosity;
    }
    
    /**
     * Adds to the numerosity of the classifier.
     *
     * @param num The added numerosity (can be negative!).
     */
    public void addNumerosity(int num)
    {
	numerosity+=num;
    }
    
    
    /**
     * Returns the time stamp of the classifier.
     */
    public int getTimeStamp()
    {
	return timeStamp;
    }

    /**
     * Sets the time stamp of the classifier.
     *
     * @param ts The new time stamp of the classifier.
     */
    public void setTimeStamp(int ts)
    {
	timeStamp=ts;
    }

    /**
     * Prints the classifier to the control panel.
     * The method prints condition action prediction predictionError fitness numerosity experience actionSetSize timeStamp.
     */
    public void printXClassifier()
    {
	System.out.println(condition+" "+action+"\t"+(float)prediction+"\t"+(float)predictionError+"\t"+(float)fitness+
			   "\t"+numerosity+"\t"+experience+"\t"+(float)actionSetSize+"\t"+timeStamp);
    }
  
    /**
     * Prints the classifier to the print writer (normally referencing a file).
     * The method prints condition action prediction predictionError fitness numerosity experience actionSetSize timeStamp.
     *
     * @param pW The writer to which the classifier is written.
     */
    public void printXClassifier(PrintWriter pW)
    {
	pW.println(condition+"-"+action+" "+(float)prediction+" "+(float)predictionError+" " +(float)fitness+
		   " "+numerosity+" "+experience+" "+(float)actionSetSize+" "+timeStamp);		
    }	
}
