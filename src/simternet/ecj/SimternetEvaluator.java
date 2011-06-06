package simternet.ecj;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.nsp.NetworkProvider;
import simternet.reporters.ApplicationProviderFitnessReporter;
import simternet.reporters.NetworkProviderFitnessReporter;
import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

/**
 * Custom evaluator uses population to create agents, injects them into
 * Simternet, and runs the simulation.
 * 
 * @author kkoning
 * 
 */
public class SimternetEvaluator extends Evaluator {

	boolean						inStep				= false;
	private static final long	serialVersionUID	= 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void evaluatePopulation(EvolutionState state) {

		/*
		 * We evaluate the fitness of our individuals by creating the
		 * corresponding Simternet agents, inserting them into the Simternet
		 * simulation environment, and running that simulation for a specified
		 * number of steps. Only after this simulation has been run can we probe
		 * the fitness of individuals.
		 * 
		 * Further, individual simulations will generally have a relatively
		 * small number of agents. (i.e., there may be 3-10 network providers,
		 * not 1,000) However, we do not want our population size, and therefore
		 * diversity, to be limited thereby. Therefore agents will need to be
		 * evaluated in groups. The membership of these groups should be random,
		 * but each of these evaluations will be independent of each other and
		 * therefore can be run in parallel.
		 */

		if (this.inStep)
			throw new RuntimeException("evaluatePopulaiton() is not reentrant, yet is being called recursively.");

		this.inStep = true;

		Parameter base = new Parameter("simternet");
		int numThreads = state.parameters.getIntWithDefault(base.push("threads"), null, 1);
		int numChunks = state.parameters.getInt(base.push("chunks"), null);
		int numSteps = state.parameters.getInt(base.push("steps"), null);
		boolean simternetCheckpoint = state.parameters.getBoolean(base.push("checkpoint"), null, false);
		int checkpointModulo = state.parameters.getInt(new Parameter("checkpoint-modulo"), null);

		// Create and initialize our simulations
		Simternet[] simternet = new Simternet[numChunks];
		for (int i = 0; i < simternet.length; i++) {
			int seed = state.random[0].nextInt();
			// System.out.println("Seed for g" + state.generation + "s" + i +
			// " = " + seed);
			simternet[i] = new Simternet(seed);
			simternet[i].start();
		}

		// Add reporters for data output

		for (int i = 0; i < simternet.length; i++) {
			// add reporters
			NetworkProviderFitnessReporter npfr = new NetworkProviderFitnessReporter(29);
			npfr.setGeneration(state.generation);
			npfr.setChunk(i);
			simternet[i].addReporter(npfr);

			ApplicationProviderFitnessReporter apfr = new ApplicationProviderFitnessReporter();
			apfr.setGeneration(state.generation);
			apfr.setChunk(i);
			simternet[i].addReporter(apfr);
		}

		// Populate them with agents
		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation sp = state.population.subpops[i];

			// Determine the type of agent represented by this subpopulation,
			// and set up the ability to create those agents.
			String agentClassName = null;
			Class agentClass = null;
			Constructor<EvolvableAgent> agentConstructor = null;
			try {
				Parameter p = new Parameter("pop").push("subpop").push(Integer.toString(i)).push("species")
						.push("agent");
				agentClassName = state.parameters.getString(p, null);
				agentClass = Class.forName(agentClassName);
				agentConstructor = agentClass.getConstructor(Simternet.class);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (agentConstructor == null)
					throw new RuntimeException("Couldn't load the class " + agentClassName);
			}

			// How many agents to a chunk?
			int numAgents = this.numAgents(sp.individuals.length, numChunks);

			// Main loop creating individuals & populating simulations
			for (int j = 0; j < sp.individuals.length; j++) {
				int whichSimternet = j / numAgents;
				EvolvableAgent agent = null;
				try {
					agent = agentConstructor.newInstance(simternet[whichSimternet]);
					agent.setIndividual(sp.individuals[j]);
				} catch (Exception e) {
					throw new RuntimeException("Couldn't instantiate agent", e);
				}

				if (agent instanceof NetworkProvider) {
					NetworkProvider nsp = (NetworkProvider) agent;
					simternet[whichSimternet].enterMarket(nsp);
				} else if (agent instanceof ApplicationProvider) {
					ApplicationProvider asp = (ApplicationProvider) agent;
					simternet[whichSimternet].enterMarket(asp);
				}
			}

		}

		// numThreads = 0;

		if (numThreads > 1) {
			// Simulations are populated. Run them!
			LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadPool = new ThreadPoolExecutor(numThreads, numThreads, 10, TimeUnit.SECONDS, tasks);
			// for (Simternet s : simternet) {
			for (int i = 0; i < simternet.length; i++) {
				Simternet s = simternet[i];
				// Save initial checkpoints
				if (simternetCheckpoint)
					if ((state.generation % checkpointModulo) == 0)
						this.generateCheckpoint(state, s, i);

				SimternetRunner sr = new SimternetRunner(s, numSteps);
				threadPool.execute(sr);
			}

			threadPool.shutdown();
			try {
				int seconds = 0; // how long we've waited
				int waiting = 60; // update every 60 seconds
				int timeout = 1200; // 20 minutes
				while (seconds < timeout) {
					if (threadPool.isTerminated()) {
						Logger.getRootLogger().info("Comleted evaluation of population");
						break;
					} else
						Logger.getRootLogger().info("Evaluating population, " + seconds + "/" + timeout + "s");
					threadPool.awaitTermination(waiting, TimeUnit.SECONDS);
					seconds += waiting;
				}
				if (!threadPool.isTerminated()) {
					Logger.getRootLogger().fatal("Could not evaluate population in under " + timeout + " seconds");
					System.exit(-1);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			for (int i = 0; i < simternet.length; i++) {
				Simternet s = simternet[i];
				SimternetRunner sr = new SimternetRunner(s, numSteps);
				if (simternetCheckpoint)
					if ((state.generation % checkpointModulo) == 0)
						this.generateCheckpoint(state, s, i);
				sr.run();
			}

		// Look at each agent and assign fitness
		SimpleProblemForm spf = (SimpleProblemForm) this.p_problem.clone();
		for (Subpopulation sp : state.population.subpops)
			for (Individual individual : sp.individuals)
				spf.evaluate(state, individual, 0, 0);

		this.inStep = false;

	}

	private void generateCheckpoint(EvolutionState state, Simternet s, int instance) {
		s.preCheckpoint();
		String filePath = state.parameters.getString(new Parameter("simternet").push("checkpoint").push("directory"),
				null);
		String fileName = "ECJ-Simternet.gen-" + state.generation + ".chunk-" + instance + ".checkpoint";
		File outputFile = new File(filePath + fileName);
		s.writeToCheckpoint(outputFile);
		s.postCheckpoint();

	}

	private int numAgents(int numIndividuals, int numChunks) {
		int agents = numIndividuals / numChunks;
		// if it doesn't divide evenly, add one so we don't have a leftover set
		// with 1 agent
		int remainder = numIndividuals % numChunks;
		if (remainder > 0)
			agents++;
		return agents;
	}

	@Override
	public boolean runComplete(EvolutionState state) {
		/*
		 * Always return false, since there seems to be no way to tell if agent
		 * behavior is ideal.
		 */
		return false;
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		if (!(this.p_problem instanceof SimpleProblemForm))
			state.output.fatal("" + this.getClass() + " used, but the Problem is not of SimpleProblemForm",
					base.push(Evaluator.P_PROBLEM));

	}

}
