package simternet.ecj;

import java.io.File;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import simternet.agents.asp.ApplicationProvider;
import simternet.agents.nsp.NetworkProvider;
import simternet.data.output.ASPInterconnectionReporter;
import simternet.data.output.ApplicationProviderFitnessReporter;
import simternet.data.output.BackboneLinkReporter;
import simternet.data.output.ECJEvolutionReporterComponent;
import simternet.data.output.EdgeDataReporter;
import simternet.data.output.EdgeMarketReporter;
import simternet.data.output.NetworkProviderFitnessReporter;
import simternet.data.output.StepReporterComponent;
import simternet.engine.AsyncDataSchedule;
import simternet.engine.Simternet;
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

	private static final long		serialVersionUID	= 1L;
	boolean							inStep				= false;

	LinkedBlockingQueue<Runnable>	tasks				= new LinkedBlockingQueue<Runnable>();
	ThreadPoolExecutor				threadPool;

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

		if (inStep)
			throw new RuntimeException("evaluatePopulaiton() is not reentrant, yet is being called recursively.");

		inStep = true;

		Parameter base = new Parameter("simternet");
		int numThreads = state.parameters.getIntWithDefault(base.push("threads"), null, 1);
		int numChunks = state.parameters.getInt(base.push("chunks"), null);
		int numSteps = state.parameters.getInt(base.push("steps"), null);
		boolean simternetCheckpoint = state.parameters.getBoolean(base.push("checkpoint"), null, false);
		int checkpointModulo = state.parameters.getInt(base.push("checkpoint-modulo"), new Parameter(
				"checkpoint-modulo"));

		// Create and initialize our simulations
		Simternet[] simternet = new Simternet[numChunks];
		for (int i = 0; i < simternet.length; i++) {
			int seed = state.random[0].nextInt();
			simternet[i] = new Simternet(seed);
			simternet[i].chunk = i;
			simternet[i].generation = state.generation;
			simternet[i].start();
		}

		// Add reporters for data output

		for (int i = 0; i < simternet.length; i++) {

			// add reporters
			// NetworkProviderFitnessReporter npfr = new
			// NetworkProviderFitnessReporter(interval);
			// npfr.setGeneration(state.generation);
			// npfr.setChunk(i);
			// simternet[i].addReporter(npfr);

			// ApplicationProviderFitnessReporter apfr = new
			// ApplicationProviderFitnessReporter(interval);
			// apfr.setGeneration(state.generation);
			// apfr.setChunk(i);
			// simternet[i].addReporter(apfr);

			// Prep the simtenet's schedule and reporter components
			AsyncDataSchedule schedule = (AsyncDataSchedule) simternet[i].schedule;
			ECJEvolutionReporterComponent eerc = new ECJEvolutionReporterComponent(simternet[i]);
			StepReporterComponent src = new StepReporterComponent(simternet[i]);

			ApplicationProviderFitnessReporter apfr = new ApplicationProviderFitnessReporter(simternet[i]);
			apfr.addComponent(eerc);
			apfr.addComponent(src);
			schedule.addReporter(apfr);

			// Network Provider Fitness Reporter
			NetworkProviderFitnessReporter npfr = new NetworkProviderFitnessReporter(simternet[i]);
			npfr.addComponent(eerc);
			npfr.addComponent(src);
			schedule.addReporter(npfr);

			// Consumer Data Reporter
			// Reporter2 cdr2 = new ConsumerDataReporter(simternet[i]);
			// cdr2.addComponent(eerc);
			// cdr2.addComponent(src);
			// schedule.addReporter(cdr2);

			// Backbone Link Reporter
			BackboneLinkReporter blr = new BackboneLinkReporter(simternet[i]);
			blr.addComponent(eerc);
			blr.addComponent(src);
			schedule.addReporter(blr);

			// ASP Interconnection Reporter
			ASPInterconnectionReporter air = new ASPInterconnectionReporter(simternet[i]);
			air.addComponent(eerc);
			air.addComponent(src);
			schedule.addReporter(air);

			// Edge Market Reporter
			EdgeMarketReporter emr = new EdgeMarketReporter(simternet[i]);
			emr.addComponent(eerc);
			emr.addComponent(src);
			schedule.addReporter(emr);

			// Edge Data Reporter
			EdgeDataReporter edr = new EdgeDataReporter(simternet[i]);
			edr.addComponent(eerc);
			edr.addComponent(src);
			schedule.addReporter(edr);

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
			int numAgents = numAgents(sp.individuals.length, numChunks);

			// Randomize the subpopulation before populating simulations
			for (int j = 0; j < sp.individuals.length; j++) {
				int randomPosition = state.random[0].nextInt(sp.individuals.length);
				Individual temp = sp.individuals[j];
				sp.individuals[j] = sp.individuals[randomPosition];
				sp.individuals[randomPosition] = temp;
			}

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
					nsp.setName("" + whichSimternet + "-" + nsp.getName());
					simternet[whichSimternet].enterMarket(nsp);
				} else if (agent instanceof ApplicationProvider) {
					ApplicationProvider asp = (ApplicationProvider) agent;
					asp.setName("" + whichSimternet + "-" + asp.getName());
					simternet[whichSimternet].enterMarket(asp);
				}
			}

		}

		// numThreads = 0;

		if (numThreads > 1) {
			// Simulations are populated. Run them!
			threadPool = new ThreadPoolExecutor(numThreads, numThreads, 10, TimeUnit.SECONDS, tasks);
			threadPool.prestartAllCoreThreads();

			// for (Simternet s : simternet) {
			for (int i = 0; i < simternet.length; i++) {
				Simternet s = simternet[i];
				// Save initial checkpoints
				if (simternetCheckpoint)
					if (state.generation % checkpointModulo == 0) {
						generateCheckpoint(state, s, i);
					}

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
					} else {
						Logger.getRootLogger().info("Evaluating population, " + seconds + "/" + timeout + "s");
					}
					threadPool.awaitTermination(waiting, TimeUnit.SECONDS);
					seconds += waiting;
				}
				if (!threadPool.isTerminated()) {
					Logger.getRootLogger().fatal("Could not evaluate population in under " + timeout + " seconds");
					System.exit(-1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < simternet.length; i++) {
				Simternet s = simternet[i];
				SimternetRunner sr = new SimternetRunner(s, numSteps);
				if (simternetCheckpoint)
					if (state.generation % checkpointModulo == 0) {
						generateCheckpoint(state, s, i);
					}
				sr.run();
			}
		}

		// Look at each agent and assign fitness
		SimpleProblemForm spf = (SimpleProblemForm) p_problem.clone();
		for (Subpopulation sp : state.population.subpops) {
			for (Individual individual : sp.individuals) {
				spf.evaluate(state, individual, 0, 0);
			}
		}

		inStep = false;

	}

	private void generateCheckpoint(EvolutionState state, Simternet s, int chunk) {
		s.preCheckpoint();
		String checkpointRootName = state.parameters.getString(
				new Parameter("simternet").push("checkpoint").push("directory"), null);

		DecimalFormat df = new DecimalFormat();
		df.setMinimumIntegerDigits(3);
		String genNumberString = df.format(state.generation);

		String genDirName = checkpointRootName.concat("gen-" + genNumberString + "/");
		File checkpointGenDir = new File(genDirName);
		if (!checkpointGenDir.exists()) {
			checkpointGenDir.mkdirs();
		}

		String checkpointFileName = "chunk-" + chunk + ".checkpoint";
		File outputFile = new File(genDirName + checkpointFileName);
		s.writeToCheckpoint(outputFile);
		s.postCheckpoint();

	}

	private int numAgents(int numIndividuals, int numChunks) {
		int agents = numIndividuals / numChunks;
		// if it doesn't divide evenly, add one so we don't have a leftover set
		// with 1 agent
		int remainder = numIndividuals % numChunks;
		if (remainder > 0) {
			agents++;
		}
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
		if (!(p_problem instanceof SimpleProblemForm)) {
			state.output.fatal("" + this.getClass() + " used, but the Problem is not of SimpleProblemForm",
					base.push(Evaluator.P_PROBLEM));
		}

	}

}
