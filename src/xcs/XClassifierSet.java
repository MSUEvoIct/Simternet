package xcs;
import java.io.Serializable;
import java.util.Vector;
import java.io.PrintWriter;

/**
 * This class handles the different sets of classifiers. It stores each set in an array. The array is initialized to 
 * a sufficient large size so that no changes in the size of the array will be necessary.
 * The class provides constructors for constructing 
 * <ul>
 * <li> the empty population,
 * <li> the match set, and
 * <li> the action set.
 * </ul>
 * It executes a GA in a set and updates classifier parameters of a set.
 * Moreover, it provides all necessary different sums and averages of parameters in the set.
 * Finally, it handles addition, deletion and subsumption of classifiers.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class XClassifierSet implements Serializable
{
    /**
     * The cons parameter is necessary for all kinds of calculations in the set. Note that it is static, so that 
     * it is not reconstructed each time a new set is created.
     */
    private static XCSConstants cons=new XCSConstants();
 
    /**
     * The Sum of the numerosity in one set is always kept up to date!
     */
    private int numerositySum;

    /**
     * Each set keeps a reference to the parent set out of which it was generated. In the population itself
     * this pointer is set to zero.
     */
    private XClassifierSet parentSet;
  
    /**
     * The classifier list (in form of an array)
     */
    private XClassifier clSet[];
    
    /**
     * The actual number of macro-classifiers in the list (which is in fact equal to the number of entries in the array).
     */
    private int cllSize;

    /**
     * Creates a new, empty population initializing the population array to the maximal population size 
     * plus the number of possible actions.
     *
     * @see XCSConstants#maxPopSize
     * @param numberOfActions The number of actions possible in the problem.
     */
    public XClassifierSet(int numberOfActions)
    {
        numerositySum=0;
        cllSize=0;
        parentSet=null;
        clSet=new XClassifier[cons.maxPopSize+numberOfActions];
    }

    /**
     * Constructs a match set out of the population. After the creation, it is checked if the match set covers all possible actions
     * in the environment. If one or more actions are not present, covering occurs, generating the missing action(s). If maximal 
     * population size is reached when covering, deletion occurs.
     *
     * @see XClassifier#XClassifier(double,int,String,int)
     * @see XCSConstants#maxPopSize
     * @see #deleteFromPopulation
     * @param state The current situation/problem instance.
     * @paramn pop The current population of classifiers.
     * @param time  The actual number of instances the XCS learned from so far.
     * @param numberOfActions The number of actions possible in the environment.     
     */
    public XClassifierSet(String state, XClassifierSet pop, int time, int numberOfActions)
    {
        parentSet=pop;
        numerositySum=0;
        cllSize=0;
        clSet=new XClassifier[pop.cllSize+numberOfActions];

        boolean[] actionCovered =  new boolean[numberOfActions];
        for(int i=0; i<actionCovered.length; i++)
            actionCovered[i]=false;

        for(int i=0; i<pop.cllSize; i++){
            XClassifier cl=pop.clSet[i];
            if( cl.match(state)){
                addClassifier(cl);
                actionCovered[cl.getAction()]=true;
            }
        }

        //Check if each action is covered. If not -> generate covering XClassifier and delete if the population is too big
        boolean again;
        do{
            again=false;
            for(int i=0; i<actionCovered.length; i++){
                if(!actionCovered[i]){
                    XClassifier newCl=new XClassifier(numerositySum+1, time, state, i);
                    
                    addClassifier(newCl);
                    pop.addClassifier(newCl);
                }
            }
            while(pop.numerositySum > cons.maxPopSize){
                XClassifier cdel=pop.deleteFromPopulation();
                // update the current match set in case a classifier was deleted out of that 
                // and redo the loop if now another action is not covered in the match set anymore.
		int pos=0;
                if(cdel!=null && (pos=containsClassifier(cdel))!=-1) {
		    numerositySum--;
		    if(cdel.getNumerosity()==0){
			removeClassifier(pos);
			if( !isActionCovered(cdel.getAction())){
			    again=true;
			    actionCovered[cdel.getAction()]=false;
			}
		    }
		}
            }
        }while(again);
    }


    /**
     * Constructs an action set out of the given match set. 
     * 
     * @param matchSet The current match set
     * @param action The chosen action for the action set.
     */
    public XClassifierSet(XClassifierSet matchSet, int action)
    {
        parentSet=matchSet;
        numerositySum=0;
        cllSize=0;
        clSet=new XClassifier[matchSet.cllSize];

        for(int i=0; i<matchSet.cllSize; i++){
            if( matchSet.clSet[i].getAction() == action){
                addClassifier(matchSet.clSet[i]);
            }
        }
    }

    /**
     * Returns the position of the classifier in the set if it is present and -1 otherwise.
     */
    private int containsClassifier(XClassifier cl)
    {
        for(int i=0; i<cllSize; i++)
            if(clSet[i]==cl)
                return i;
        return -1;
    }

    /**
     * Returns if the specified action is covered in this set.
     */
    private boolean isActionCovered(int action)
    {
        for(int i=0; i<cllSize; i++){
            if( clSet[i].getAction() == action)
                return true;
        }
        return false;
    }

    /**
     * Updates all parameters in the current set (should be the action set).
     * Essentially, reinforcement Learning as well as the fitness evaluation takes place in this set.
     * Moreover, the prediction error and the action set size estimate is updated. Also, 
     * action set subsumption takes place if selected. As in the algorithmic description, the fitness is updated
     * after prediction and prediction error. However, in order to be more conservative the prediction error is 
     * updated before the prediction.
     *
     * @see XCSConstants#gamma
     * @see XClassifier#increaseExperience
     * @see XClassifier#updatePreError
     * @see XClassifier#updatePrediction
     * @see XClassifier#updateActionSetSize
     * @see #updateFitnessSet
     * @see XCSConstants#doActionSetSubsumption
     * @see #doActionSetSubsumption
     * @param maxPrediction The maximum prediction value in the successive prediction array 
     * (should be set to zero in single step environments).
     * @param reward The actual resulting reward after the execution of an action.
     */
    public void updateSet(double maxPrediction, double reward)
    {
    
        double P=reward + cons.gamma*maxPrediction;
    
        for(int i=0; i<cllSize; i++){
            clSet[i].increaseExperience();
            clSet[i].updatePreError(P);
            clSet[i].updatePrediction(P);
            clSet[i].updateActionSetSize(numerositySum);
        }
        updateFitnessSet();

        if(cons.doActionSetSubsumption)
            doActionSetSubsumption();
    }
    
    /**
     * Special function for updating the fitnesses of the classifiers in the set.
     *
     * @see XClassifier#updateFitness
     */
    private void updateFitnessSet()
    {
        double accuracySum=0.;
        double []accuracies = new double[cllSize];

        //First, calculate the accuracies of the classifier and the accuracy sums
        for(int i=0; i<cllSize; i++){
            accuracies[i]= clSet[i].getAccuracy();
            accuracySum+=accuracies[i]*clSet[i].getNumerosity();
        }
    
        //Next, update the fitnesses accordingly
        for(int i=0; i<cllSize; i++){
            clSet[i].updateFitness(accuracySum, accuracies[i]);
        }
    }
  
    /**
     * The Genetic Discovery in XCS takes place here. If a GA takes place, two classifiers are selected
     * by roulette wheel selection, possibly crossed and mutated and then inserted.
     *
     * @see XCSConstants#theta_GA
     * @see #selectXClassifierRW
     * @see XClassifier#twoPointCrossover
     * @see XClassifier#applyMutation
     * @see XCSConstants#predictionErrorReduction
     * @see XCSConstants#fitnessReduction
     * @see #insertDiscoveredXClassifiers
     * @param time  The actual number of instances the XCS learned from so far.
     * @param state  The current situation/problem instance.
     * @param numberOfActions The number of actions possible in the environment.
     */ 
    public void runGA(int time, String state, int numberOfActions)
    {
        // Don't do a GA if the theta_GA threshold is not reached, yet
        if( cllSize==0 || time-getTimeStampAverage() < cons.theta_GA )
            return;
    
        setTimeStamps(time);

        double fitSum=getFitnessSum(); 
        // Select two XClassifiers with roulette Wheel Selection
        XClassifier cl1P=selectXClassifierRW(fitSum);
        XClassifier cl2P=selectXClassifierRW(fitSum);
    
        XClassifier cl1=new XClassifier(cl1P);
        XClassifier cl2=new XClassifier(cl2P);
    
        cl1.twoPointCrossover(cl2);

        cl1.applyMutation(state, numberOfActions);
        cl2.applyMutation(state, numberOfActions);
        
        cl1.setPrediction((cl1.getPrediction() + cl2.getPrediction())/2.);
        cl1.setPredictionError(cons.predictionErrorReduction * (cl1.getPredictionError() + cl2.getPredictionError())/2.);
        cl1.setFitness(cons.fitnessReduction * (cl1.getFitness() + cl2.getFitness())/2.);
        cl2.setPrediction(cl1.getPrediction());
        cl2.setPredictionError(cl1.getPredictionError());
        cl2.setFitness(cl1.getFitness());
   
        insertDiscoveredXClassifiers(cl1, cl2, cl1P, cl2P);
    }

    /**
     * Selects one classifier using roulette wheel selection according to the fitnesses of the classifiers.
     */
    private XClassifier selectXClassifierRW(double fitSum)
    {
        double choiceP=cons.drand()*fitSum;
        int i=0; 
        double sum=clSet[i].getFitness();
        while(choiceP>sum){
            i++;
            sum+=clSet[i].getFitness();
        }

        return clSet[i];
    }
  
    /**
     * Inserts both discovered classifiers keeping the maximal size of the population and possibly doing GA subsumption.
     *
     * @see XCSConstants#doGASubsumption
     * @see #subsumeXClassifier
     * @see #addXClassifierToPopulation
     * @see XCSConstants#maxPopSize
     * @see #deleteFromPopulation
     * @param cl1 The first classifier generated by the GA.
     * @param cl2 The second classifier generated by the GA.
     * @param cl1P The first parent of the two new classifiers.
     * @param cl2P The second classifier of the two new classifiers.
     */
    private void insertDiscoveredXClassifiers(XClassifier cl1, XClassifier cl2, XClassifier cl1P, XClassifier cl2P)
    {
        XClassifierSet pop=this;
        while(pop.parentSet!=null)
            pop=pop.parentSet;

        if(cons.doGASubsumption){
            subsumeXClassifier(cl1, cl1P, cl2P);        
            subsumeXClassifier(cl2, cl1P, cl2P);        
        }else{
	    pop.addXClassifierToPopulation(cl1);
            pop.addXClassifierToPopulation(cl2);
        }
        
        while(pop.numerositySum > cons.maxPopSize)
            pop.deleteFromPopulation();
    }
  
    /**
     * Tries to subsume a classifier in the parents.
     * If no subsumption is possible it tries to subsume it in the current set.
     *
     * @see #subsumeXClassifier(XClassifier)
     */
    private void subsumeXClassifier(XClassifier cl, XClassifier cl1P, XClassifier cl2P)
    {
        if(cl1P!=null && cl1P.subsumes(cl)){
            increaseNumerositySum(1);
            cl1P.addNumerosity(1);
        }else if(cl2P!=null && cl2P.subsumes(cl)){
            increaseNumerositySum(1);
            cl2P.addNumerosity(1);
        }else{
            subsumeXClassifier(cl); //calls second subsumeXClassifier fkt!
        }            
    }

    /**
     * Tries to subsume a classifier in the current set. 
     * This method is normally called in an action set. 
     * If no subsumption is possible the classifier is simply added to the population considering 
     * the possibility that there exists an identical classifier.
     *
     * @param cl The classifier that may be subsumed.
     * @see #addXClassifierToPopulation
     */
    private void subsumeXClassifier(XClassifier cl)
    {
        //Open up a new Vector in order to chose the subsumer candidates randomly
        Vector choices= new Vector();
        for(int i=0; i<cllSize; i++){
            if( clSet[i].subsumes(cl) )
                choices.addElement(clSet[i]);
        }

        if(choices.size()>0){
            int choice=(int)((double)cons.drand()*choices.size());
            ((XClassifier)choices.elementAt(choice)).addNumerosity(1);
            increaseNumerositySum(1);
            return;
        }
	//If no subsumer was found, add the classifier to the population
        addXClassifierToPopulation(cl);
    }

    /**
     * Executes action set subsumption. 
     * The action set subsumption looks for the most general subsumer classifier in the action set 
     * and subsumes all classifiers that are more specific than the selected one.
     *
     * @see XClassifier#isSubsumer
     * @see XClassifier#isMoreGeneral
     */
    private void doActionSetSubsumption()
    {
        XClassifierSet pop=this;
        while(pop.parentSet!=null)
            pop=pop.parentSet;
        
        XClassifier subsumer=null;
        for(int i=0; i<cllSize; i++){
            if(clSet[i].isSubsumer())
                if(subsumer==null || clSet[i].isMoreGeneral(subsumer))
                    subsumer=clSet[i];
        }

	//If a subsumer was found, subsume all more specific classifiers in the action set
        if(subsumer!=null){
	    for(int i=0; i<cllSize; i++){
		if(subsumer.isMoreGeneral(clSet[i])){
		    int num=clSet[i].getNumerosity();
		    subsumer.addNumerosity(num);
		    clSet[i].addNumerosity((-1)*num);
		    pop.removeClassifier(clSet[i]);
		    removeClassifier(i);
		    i--;
		}
	    }
	}
     }

    /**
     * Adds the classifier to the population and checks if an identical classifier exists. 
     * If an identical classifier exists, its numerosity is increased.
     *
     * @see #getIdenticalClassifier
     * @param cl The to be added classifier.
     */
    private void addXClassifierToPopulation(XClassifier cl)
    {
        // set pop to the actual population
        XClassifierSet pop=this;
        while(pop.parentSet!=null)
            pop=pop.parentSet;
    
        XClassifier oldcl=null;
        if((oldcl=pop.getIdenticalClassifier(cl))!=null){
            oldcl.addNumerosity(1);
            increaseNumerositySum(1);
        }else{
            pop.addClassifier(cl);
        }
    }

    /**
     * Looks for an identical classifier in the population.
     *
     * @param newCl The new classifier.
     * @return Returns the identical classifier if found, null otherwise.
     */
    private XClassifier getIdenticalClassifier(XClassifier newCl)
    {
        for(int i=0; i<cllSize; i++)
            if(newCl.equals(clSet[i]))
                return clSet[i];
        return null;
    }

    /**
     * Deletes one classifier in the population.
     * The classifier that will be deleted is chosen by roulette wheel selection 
     * considering the deletion vote. Returns the macro-classifier which got decreased by one micro-classifier.
     * 
     * @see XClassifier#getDelProp    
     */
    private XClassifier deleteFromPopulation()
    {
        double meanFitness= getFitnessSum()/(double)numerositySum;
        double sum=0.;
        for(int i=0; i<cllSize; i++){
            sum += clSet[i].getDelProp(meanFitness);
        }

        double choicePoint=sum*cons.drand();
        sum=0.;
        for(int i=0; i<cllSize; i++){
            sum += clSet[i].getDelProp(meanFitness);
            if(sum > choicePoint){
                XClassifier cl=clSet[i];
		cl.addNumerosity(-1);
		numerositySum--;
                if(cl.getNumerosity()==0){
		    removeClassifier(i);
		}
		return cl;
            }
        }
        return null;
    }

    /**
     * Updates the numerositySum of the set and deletes all classifiers with numerosity 0.
     */
    public void confirmClassifiersInSet()
    {
	int copyStep=0;
	numerositySum=0;
	int i;
	for(i=0; i<cllSize-copyStep; i++){
	    if(clSet[i+copyStep].getNumerosity()==0){
		copyStep++;
		i--;
	    }else{
		if(copyStep>0){
		    clSet[i]=clSet[i+copyStep];
		}
		numerositySum+=clSet[i].getNumerosity();
	    }
	}
	for( ; i<cllSize; i++){
	    clSet[i]=null;
	}
	cllSize -= copyStep;
    }


    /**
     * Sets the time stamp of all classifiers in the set to the current time. The current time 
     * is the number of exploration steps executed so far.
     *
     * @param time The actual number of instances the XCS learned from so far.
     */
    private void setTimeStamps(int time)
    {
        for(int i=0; i<cllSize; i++)
            clSet[i].setTimeStamp(time);
    }

    /** 
     * Adds a classifier to the set and increases the numerositySum value accordingly.
     *
     * @param classifier The to be added classifier.
     */
    private void addClassifier(XClassifier classifier)
    {
        clSet[cllSize]=classifier;
        addValues(classifier);
        cllSize++;
    }


    /**
     * Increases the numerositySum value with the numerosity of the classifier.
     */
    private void addValues(XClassifier cl)
    {
        numerositySum+=cl.getNumerosity();      
    }
    
    /**
     * Increases recursively all numerositySum values in the set and all parent sets.
     * This function should be called when the numerosity of a classifier in some set is increased in 
     * order to keep the numerosity sums of all sets and essentially the population up to date.
     */
    private void increaseNumerositySum(int nr)
    {
        numerositySum+=nr;
        if(parentSet!=null)
            parentSet.increaseNumerositySum(nr);
    }

    /**
     * Removes the specified (possible macro-) classifier from the population.
     * The function returns true when the classifier was found and removed and false
     * otherwise. It does not update the numerosity sum of the set, neither 
     * recursively remove classifiers in the parent set. This must be done manually where required.
     */
    private boolean removeClassifier(XClassifier classifier)
    {
        int i;
        for(i=0; i<cllSize; i++)
            if(clSet[i]==classifier)
                break;  
	if(i==cllSize){
	    return false;
	}
        for( ; i<cllSize-1; i++)
            clSet[i]=clSet[i+1];
        clSet[i]=null;

        cllSize--;

        return true;
    }

    /**
     * Removes the (possible macro-) classifier at the specified array position from the population.
     * The function returns true when the classifier was found and removed and false
     * otherwise. It does not update the numerosity of the set, neither 
     * recursively remove classifiers in the parent set. This must be done manually where required.
     */
    private boolean removeClassifier(int pos)
    {
        int i;
        for(i=pos ; i<cllSize-1; i++)
            clSet[i]=clSet[i+1];
        clSet[i]=null;
        cllSize--;

        return true;
    }

    /**
     * Returns the sum of the prediction values of all classifiers in the set.
     */
    private double getPredictionSum()
    {
        double sum=0.;

        for(int i=0; i<cllSize; i++){
            sum+=clSet[i].getPrediction() * clSet[i].getNumerosity();
        }
        return sum;
    }

    /**
     * Returns the sum of the fitnesses of all classifiers in the set.
     */
    private double getFitnessSum()
    {
        double sum=0.;

        for(int i=0; i<cllSize; i++)
            sum+=clSet[i].getFitness();
        return sum;
    }

    /**
     * Returns the sum of the time stamps of all classifiers in the set.
     */
    private double getTimeStampSum()
    {
        double sum=0.;

        for(int i=0; i<cllSize; i++){
            sum+=clSet[i].getTimeStamp() * clSet[i].getNumerosity();
        }
        return sum;
    }

    /**
     * Returns the number of micro-classifiers in the set.
     */
    public int getNumerositySum()
    {
        return numerositySum;
    }

    /**
     * Returns the classifier at the specified position.
     */
    public XClassifier elementAt(int i)
    {
        return clSet[i];
    }

    /**
     * Returns the number of macro-classifiers in the set.
     */
    public int getSize()
    {
        return cllSize;
    }

    /**
     * Returns the average of the time stamps in the set.
     */
    private double getTimeStampAverage()
    {
        return getTimeStampSum()/numerositySum;
    }

    /**
     * Prints the classifier set to the control panel.
     */
    public void printSet()
    {
        System.out.println("Averages:");
        System.out.println("Pre: "+(getPredictionSum()/numerositySum)+ " Fit: "+(getFitnessSum()/numerositySum) + " Tss: "+ (getTimeStampSum()/numerositySum) + " Num: " + numerositySum);
        for(int i=0; i<cllSize; i++){
            clSet[i].printXClassifier();
        }       
    }

    /**
     * Prints the classifier set to the specified print writer (which usually refers to a file).
     *
     * @param pW The print writer that normally refers to a file writer.
     */
    public void printSet(PrintWriter pW)
    {
        pW.println("Averages:");
        pW.println("Pre: "+(getPredictionSum()/numerositySum)+ " Fit: "+(getFitnessSum()/numerositySum) + " Tss: "+ (getTimeStampSum()/numerositySum) + " Num: " + numerositySum);
        for(int i=0; i<cllSize; i++){
            clSet[i].printXClassifier(pW);
        }       
    }
}
