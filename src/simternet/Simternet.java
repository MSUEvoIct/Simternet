package simternet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import simternet.asp.ASP;
import simternet.asp.ASPIndividual;
import simternet.consumer.Consumer;
import simternet.consumer.ConsumerIndividual;
import simternet.consumer.PopulationInitializer;
import simternet.nsp.NSP;
import simternet.nsp.NSPIndividual;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.agency.eval.AgencyModel;
import ec.agency.eval.EvaluationGroup;
import ec.agency.io.DataOutputFile;
import ec.agency.io.GenerationAggregatingDataOutputFile;
import ec.simple.SimpleFitness;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class Simternet extends SimState implements AgencyModel, Steppable {
	private static final long serialVersionUID = 1L;
	static final Parameter pRoot = new Parameter("eval.model");

	/**********************
	 * Economic Variables *
	 **********************/

	// Landscape Variables
	public int landscapeSizeX;
	public int landscapeSizeY;

	// Finance varialbes
	public double interestRate;
	public double depreciationRate;
	public double payoffRate;

	// NSP variables
	public double nspEndowment;
	public double edgeBuildCostFixed;
	public double edgeBuildCostPerUser;
	public double edgeOpCostFixed;
	public double edgeOpCostPerUser;
	public double edgeInitialBandwidth;
	public double congestionAdjustmentSpeed;

	// ASP variables
	public double aspEndowment;
	public double qualityPrice;
	public double qualityToBandwidthExponent;
	public double applicationFlowGrowthProportion;

	// Consumer Variables
	public PopulationInitializer populationInitializer;
	public double qualityExponent;
	public double preferenceExponent;
	public double wtpExponent;
	public double appBudget;

	/*************************
	 * Operational Variables *
	 *************************/

	// Give Simternet an awareness of its place in an EC run.
	int job = 0;
	int generation;
	int simulationID;
	int steps;

	// Here to prevent having to generate it every time
	Int2D[] allLocations;

	// Used to track if Simternet has been properly initialized
	boolean beenSeeded = false;
	boolean beenSetup = false;

	// Controls the order of execution of the agents.
	static final int nspOrder = 1;
	static final int aspOrder = 2;
	static final int consumerOrder = 3;
	static final int simternetOrder = 4;

	// Lists of Agents
	public ASP[] allASPs;
	public NSP[] allNSPs;
	public Consumer[] allConsumers;
	int aspIDs, nspIDs, consumerIDs;

	// Misc
	public static final DecimalFormat nf = new DecimalFormat("0.###E0");

	// Data Output
	public static DataOutputFile out = null;

	/**
	 * Initializes the simulation with a default seed. A no-arg constructor is
	 * needed to fit the ec.Setup pattern used in ECJ and Agency. A specific
	 * random seed should be set, and run() throws an error message if it has
	 * not.
	 */
	public Simternet() {
		super(-1);
		schedule.scheduleRepeating(Schedule.EPOCH, simternetOrder, this);
	}

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		setup(evoState.parameters, base);
		job = (Integer) evoState.job[0];
	}

	public void setup(ParameterDatabase pd, Parameter base) {
		landscapeSizeX = pd.getInt(pRoot.push("landscapeSizeX"), null);
		landscapeSizeY = pd.getInt(pRoot.push("landscapeSizeY"), null);

		interestRate = pd.getFloat(pRoot.push("interestRate"), null);
		depreciationRate = pd.getFloat(pRoot.push("depreciationRate"), null);
		payoffRate = pd.getFloat(pRoot.push("payoffRate"), null);

		nspEndowment = pd.getFloat(pRoot.push("nspEndowment"), null);
		edgeBuildCostFixed = pd
				.getFloat(pRoot.push("edgeBuildCostFixed"), null);
		edgeBuildCostPerUser = pd.getFloat(pRoot.push("edgeBuildCostPerUser"),
				null);
		edgeOpCostFixed = pd.getFloat(pRoot.push("edgeOpCostFixed"), null);
		edgeOpCostPerUser = pd.getFloat(pRoot.push("edgeOpCostPerUser"), null);
		edgeInitialBandwidth = pd.getFloat(pRoot.push("edgeInitialBandwidth"),
				null);
		congestionAdjustmentSpeed = pd.getFloat(
				pRoot.push("congestionAdjustmentSpeed"), null);

		// ASP Variables
		aspEndowment = pd.getFloat(pRoot.push("aspEndowment"), null);

		qualityPrice = pd.getFloat(pRoot.push("qualityPrice"), null);

		qualityToBandwidthExponent = pd.getFloat(
				pRoot.push("qtyToBandwidthExponent"), null);

		applicationFlowGrowthProportion = pd.getFloat(
				pRoot.push("applicationFlowGrowthProportion"), null);

		// Consumer Variables
		qualityExponent = pd.getFloat(pRoot.push("qualityExponent"), null);
		preferenceExponent = pd
				.getFloat(pRoot.push("preferenceExponent"), null);
		wtpExponent = pd.getFloat(pRoot.push("wtpExponent"), null);
		appBudget = pd.getFloat(pRoot.push("appBudget"), null);

		steps = pd.getInt(pRoot.push("steps"), null);

		// Set up population initializer
		try {
			Parameter pPopInit = pRoot.push("populationInitializer");
			Class popInitClass = (Class) pd.getClassForParameter(pPopInit,
					null, PopulationInitializer.class);
			populationInitializer = (PopulationInitializer) popInitClass
					.newInstance();
			populationInitializer.setup(pd, pPopInit);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		initOutput();
		
		beenSetup = true;
	}

	private void initOutput() {
		if (Simternet.out == null) {
			String fileName = "Simternet.out.job" + job;
			String[] colNames = new String[6];
			colNames[0] = "Generation";
			colNames[1] = "aspInvestment";
			colNames[2] = "aspProfit";
			colNames[3] = "nspInvestment";
			colNames[4] = "nspProfit";
			colNames[5] = "consumerSurplus";
			Simternet.out = new GenerationAggregatingDataOutputFile(fileName,
					colNames);
		}
	}

	@Override
	public void run() {
		sanityCheck();

		for (int step = 0; step < steps; step++) {
			this.schedule.step(this);
			// TODO: Does anything else need to be run besides the MASON
			// schedule?
		}

		// Output some data
		Object[] data = new Object[6];
		data[0] = generation;

		// ASP Total Investment
		// ASP Total Profits
		double aspTotalInvestment = 0;
		double aspTotalProfit = 0;
		for (int i = 0; i < allASPs.length; i++) {
			aspTotalProfit += allASPs[i].financials.getBalance();
			aspTotalInvestment += allASPs[i].financials.totalInvested;
		}
		data[1] = aspTotalInvestment;
		data[2] = aspTotalProfit;

		// NSP Total Profits
		// NSP Total Investment
		double nspTotalInvestment = 0;
		double nspTotalProfit = 0;
		for (int i = 0; i < allNSPs.length; i++) {
			nspTotalProfit += allNSPs[i].financials.getBalance();
			nspTotalInvestment += allNSPs[i].financials.totalInvested;
		}
		data[3] = nspTotalInvestment;
		data[4] = nspTotalProfit;

		// Consumer Surplus
		double totalConsumerSurplus = 0;
		for (int i = 0; i < allConsumers.length; i++) {
			totalConsumerSurplus += allConsumers[i].totalSurplus;
		}
		data[5] = totalConsumerSurplus;
		
		out.writeTuple(data);

	}

	public Int2D[] getAllLocations() {
		if (this.allLocations == null) {
			this.allLocations = new Int2D[landscapeSizeX * landscapeSizeY];
			for (int x = 0; x < landscapeSizeX; x++)
				for (int y = 0; y < landscapeSizeY; y++)
					allLocations[y * landscapeSizeX + x] = new Int2D(x, y);
		}

		return this.allLocations;
	}

	@Override
	public void setEvaluationGroup(EvaluationGroup evalGroup) {
		List<ASPIndividual> aspInds = new ArrayList<ASPIndividual>();
		List<NSPIndividual> nspInds = new ArrayList<NSPIndividual>();
		List<ConsumerIndividual> consInds = new ArrayList<ConsumerIndividual>();

		for (Individual i : evalGroup.individuals) {
			if (i instanceof ASPIndividual)
				aspInds.add((ASPIndividual) i);
			else if (i instanceof NSPIndividual)
				nspInds.add((NSPIndividual) i);
			else if (i instanceof ConsumerIndividual)
				consInds.add((ConsumerIndividual) i);
			else
				throw new RuntimeException("Don't know how to evaluate "
						+ i.getClass().getCanonicalName());
		}

		byte i = 0;
		allASPs = new ASP[aspInds.size()];
		for (ASPIndividual aspInd : aspInds) {
			ASP asp = createASP(aspInd, i);
			this.schedule.scheduleRepeating(asp);
			allASPs[i] = asp;
			i++;
		}

		i = 0;
		allNSPs = new NSP[nspInds.size()];
		for (NSPIndividual nspInd : nspInds) {
			NSP nsp = createNSP(nspInd, i);
			this.schedule.scheduleRepeating(nsp);
			allNSPs[i] = nsp;
			i++;
		}

		i = 0;
		allConsumers = new Consumer[consInds.size()];
		for (ConsumerIndividual consInd : consInds) {
			Consumer c = createConsumer(consInd, i);
			this.schedule.scheduleRepeating(c);
			allConsumers[i] = c;
			i++;
		}

	}

	protected NSP createNSP(NSPIndividual nspInd, byte nspID) {
		NSP nsp = new NSP(this, nspInd, nspID);
		return nsp;
	}

	protected ASP createASP(ASPIndividual aspInd, byte aspID) {
		ASP asp = new ASP(this, aspInd, aspID);
		asp.specialization = this.random.nextDouble();
		return asp;
	}

	protected Consumer createConsumer(ConsumerIndividual consInd, byte consID) {
		Consumer cons = new Consumer(this, consInd, consID);
		// The full arrays for NSPs and ASPs must have been chosen by this
		// point,
		// because we need those sizes to initialize consumer data structures

		return cons;
	}

	@Override
	public void step(SimState s) {
		// TODO Auto-generated method stub

	}

	/**
	 * Pre-flight checks to ensure that some configuration and operational
	 * variables have been set to sensical values.
	 */
	void sanityCheck() {
		// Have necessary configuration functions been called?
		if (!beenSeeded)
			throw new RuntimeException("Disallowing default seeding");
		if (!beenSetup)
			throw new RuntimeException("Disallowing run w/o setup!");

		// Have economic variables been initialized to sensical values?
		if (landscapeSizeX <= 0)
			throw new RuntimeException();
		if (landscapeSizeY <= 0)
			throw new RuntimeException();
		if (interestRate <= 0)
			throw new RuntimeException();
		if (depreciationRate <= 0)
			throw new RuntimeException();
		if (nspEndowment <= 0)
			throw new RuntimeException();
		if (edgeBuildCostFixed <= 0)
			throw new RuntimeException();
		if (edgeBuildCostPerUser <= 0)
			throw new RuntimeException();
		if (edgeOpCostFixed < 0)
			throw new RuntimeException();
		if (edgeOpCostPerUser < 0)
			throw new RuntimeException();
		if (aspEndowment <= 0)
			throw new RuntimeException();

		// Have the list of agents been created, and are they of non-zero size?
		if (allASPs == null)
			throw new RuntimeException();
		if (allASPs.length == 0)
			throw new RuntimeException();
		if (allNSPs == null)
			throw new RuntimeException();
		if (allNSPs.length == 0)
			throw new RuntimeException();
		if (allConsumers == null)
			throw new RuntimeException();
		if (allConsumers.length == 0)
			throw new RuntimeException();

	}

	/*******************************
	 * Trivial getters and setters *
	 *******************************/
	@Override
	public void setSeed(int seed) {
		super.setSeed(seed);
		beenSeeded = true;
	}

	@Override
	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

	@Override
	public Integer getGeneration() {
		return generation;
	}

	@Override
	public void setSimulationID(Integer simulationID) {
		this.simulationID = simulationID;
	}

	@Override
	public Integer getSimulationID() {
		return simulationID;
	}

	@Override
	public Map<Individual, Fitness> getFitnesses() {
		Map<Individual, Fitness> fitnesses = new IdentityHashMap<Individual, Fitness>();

		// put in all the ASPs
		for (byte aspID = 0; aspID < allASPs.length; aspID++) {
			Individual ind = (Individual) allASPs[aspID].ind;
			SimpleFitness fit = new SimpleFitness();
			float fitMeasure = (float) allASPs[aspID].financials.getBalance();
			fit.setFitness(null, fitMeasure, false);
			fitnesses.put(ind, fit);
		}

		// put in all the NSPs
		for (byte nspID = 0; nspID < allNSPs.length; nspID++) {
			Individual ind = (Individual) allNSPs[nspID].ind;
			SimpleFitness fit = new SimpleFitness();
			float fitMeasure = (float) allNSPs[nspID].financials.getBalance();
			fit.setFitness(null, fitMeasure, false);
			fitnesses.put(ind, fit);
		}

		return fitnesses;
	}

	public double getTotalPopulation(byte x, byte y) {
		double totalPopulation = 0;
		for (Consumer c : allConsumers)
			totalPopulation += c.population[x][y];
		return totalPopulation;
	}

}
