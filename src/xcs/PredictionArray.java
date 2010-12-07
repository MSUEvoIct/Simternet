package xcs;

import java.io.Serializable;
import java.util.ArrayList;

import xcs.util.RandomNumber;

/**
 * This class generates a prediction array of the provided set. The prediction
 * array is generated according to Wilson's Classifier Fitness Based on Accuracy
 * (Evolutionary Computation Journal, 1995). Moreover, this class provides all
 * methods to handle selection in the prediction array, essentially, to select
 * the best action, a present random action or an action by roulette wheel
 * selection.
 * 
 * @author Martin V. Butz
 * @version XCSJava 1.0
 * @since JDK1.1
 */
public class PredictionArray implements Serializable {
	/**
	 * Holds the candidate solutions
	 */
	private ArrayList<ArrayList<XClassifier>> candidateSolutions;

	/**
	 * The sum of the fitnesses of classifiers that represent each entry in the
	 * prediction array.
	 */
	private double[] nr;

	/**
	 * The prediction array.
	 */
	private double[] pa;

	private int size;

	/**
	 * Holds the current state information
	 */
	private AgentData state;

	private XClassifier[] xc;

	/**
	 * Constructs the prediction array according to the current set and the
	 * possible number of actions.
	 * 
	 * @param set
	 *            The classifier set out of which a prediction array is formed
	 *            (normally the match set).
	 */
	public PredictionArray(XClassifierSet set) {
		this.size = set.getSize();
		this.state = set.getState();
		this.pa = new double[this.size];
		this.nr = new double[this.size];
		this.xc = new XClassifier[this.size];

		for (int i = 0; i < this.size; i++) {
			this.pa[i] = 0.;
			this.nr[i] = 0.;
		}
		for (int i = 0; i < set.getSize(); i++) {
			XClassifier cl = set.elementAt(i);
			this.pa[i] = (cl.getPrediction() * cl.getFitness());
			this.nr[i] = cl.getFitness();
			this.xc[i] = cl;
		}
		for (int i = 0; i < this.size; i++)
			if (this.nr[i] != 0)
				this.pa[i] /= this.nr[i];
			else
				this.pa[i] = 0;
		this.formCandidateSet();
	}

	/**
	 * Selects the action in the prediction array with the best value.
	 */
	public ArrayList<XClassifier> bestActionWinner() {
		// for now, just due pure random search
		return this.randomActionWinner();
	}

	/**
	 * Defuzzifies multiple classifiers and finds their average
	 */
	public double defuzzifyClassifiers(ArrayList<XClassifier> set) {
		double output = 0.0;
		for (XClassifier x : set)
			output = this.state.defuzzify(x.getCondition() + x.getAction());
		if (output == 0)
			return 0.0;
		return output / set.size(); // Take average
	}

	/**
	 * Checks to see if an XClassifier is both consistent and non-redundant with
	 * every member of a set of XClassifiers
	 */
	public boolean fitsInSet(ArrayList<XClassifier> r, XClassifier x) {
		for (XClassifier classifier : r)
			if (!classifier.isConsistent(x) || !classifier.isNonRedundant(x))
				return false;
		return true;
	}

	/**
	 * Selects the candidate set for either exploration/exploitation
	 */
	public void formCandidateSet() {
		// Fuzzy-XCS dictates that order should be shuffled, ignoring that for
		// now
		this.candidateSolutions = new ArrayList<ArrayList<XClassifier>>();
		for (int i = 0; i < this.size; i++) {
			ArrayList<XClassifier> currentR = new ArrayList<XClassifier>();
			currentR.add(this.xc[i]);
			for (int j = 0; j < this.size; j++) {
				if (i == j)
					continue;
				if (this.fitsInSet(currentR, this.xc[j]))
					currentR.add(this.xc[j]);
			}
			this.candidateSolutions.add(i, currentR);
		}
		// TODO: Check for duplicates
	}

	/**
	 * Returns the highest value in the prediction array.
	 */
	public double getBestValue() {
		int i;
		double max;
		for (i = 1, max = this.pa[0]; i < this.pa.length; i++)
			if (max < this.pa[i])
				max = this.pa[i];
		return max;
	}

	/*************** Action selection functions ****************/

	/**
	 * Returns the average value of the specified set of classifiers
	 */
	public double getValue(ArrayList<XClassifier> set) {
		int count = 0;
		double sum = 0;
		for (XClassifier classifier : set)
			for (int i = 0; i < this.size; i++)
				if (classifier == this.xc[i]) {
					count++;
					sum += this.pa[i];
					continue;
				}
		if (sum == 0.0)
			return 0.0;
		return sum / count;
	}

	/**
	 * Selects an action randomly. The function assures that the chosen action
	 * is represented by at least one classifier.
	 */
	public ArrayList<XClassifier> randomActionWinner() {
		// Select from candidate set
		// return defuzifyClassifiers double
		int index = (int) Math.round(RandomNumber.getDouble()
				* this.candidateSolutions.size());
		return this.candidateSolutions.get(index);
	}

	/**
	 * Selects an action in the prediction array by roulette wheel selection.
	 */
	public int rouletteActionWinner() {
		double bidSum = 0.;
		int i;
		for (i = 0; i < this.pa.length; i++)
			bidSum += this.pa[i];

		bidSum *= XCSConstants.drand();
		double bidC = 0.;
		for (i = 0; bidC < bidSum; i++)
			bidC += this.pa[i];
		return i;
	}
}
