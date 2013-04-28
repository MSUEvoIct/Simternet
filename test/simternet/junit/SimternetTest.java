package simternet.junit;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import simternet.Simternet;
import simternet.asp.ASP;
import simternet.asp.UnitASPIndividual;
import simternet.consumer.Consumer;
import simternet.consumer.RationalConsumer;
import simternet.nsp.NSP;
import simternet.nsp.UnitNSPIndividual;
import ec.Fitness;
import ec.Individual;
import ec.agency.eval.EvaluationGroup;
import ec.agency.util.IdentitySet;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class SimternetTest {

	@Test
	public void testSetup() {
		Simternet s = SimternetTest.basicSimternet();
		EvaluationGroup eg = SimternetTest.testEvalGroup(5, 5, 5);
		s.setEvaluationGroup(eg);
	}

	@Test
	public void testRun() {
		Simternet s = SimternetTest.basicSimternet();
		EvaluationGroup eg = SimternetTest.testEvalGroup(5, 5, 5);
		s.setEvaluationGroup(eg);

		s.run();

	}

	@Test
	public void testSetSeedInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFitnesses() {
		Simternet s = SimternetTest.basicSimternet();
		EvaluationGroup eg = SimternetTest.testEvalGroup(5, 5, 5);
		s.setEvaluationGroup(eg);

		s.run();
		Map<Individual, Fitness> fitnesses = s.getFitnesses();

		if (fitnesses.size() < 10)
			fail("missing some fitnesses");

		// Make sure they're all different individuals
		Set<Individual> allInds = new IdentitySet();
		for (Entry<Individual, Fitness> entry : fitnesses.entrySet()) {
			if (allInds.contains(entry.getKey()))
				fail("duplicate individual detected");

			Fitness fit = entry.getValue();
			if (fit == null)
				fail("null fitness");

			if (true) { // debug print fitness output

				System.out.println(entry.getKey().getClass().getName() + " = "
						+ entry.getValue().fitness());
			}

		}

	}

	@Test
	public void testAgentIDs() {
		Simternet s = SimternetTest.basicSimternet();
		EvaluationGroup eg = SimternetTest.testEvalGroup(5, 5, 5);
		s.setEvaluationGroup(eg);

		int i = 0;
		for (Consumer c : s.allConsumers) {
			if (c.id != i++)
				fail("Agent ID does not match expected value");
		}

		i = 0;
		for (ASP asp : s.allASPs) {
			if (asp.id != i++)
				fail("Agent ID does not match expected value");
		}

		i = 0;
		for (NSP nsp : s.allNSPs) {
			if (nsp.id != i++)
				fail("Agent ID does not match expected value");
		}
	}

	public static Simternet basicSimternet() {
		Simternet s = new Simternet();
		s.setSeed(0);
		s.setGeneration(0);
		s.setJob(0);
		s.setSimulationID(0);

		// Initialize with test parameter database
		ParameterDatabase pd;
		try {
			pd = new ParameterDatabase("simternet.test.properties",
					SimternetTest.class);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		s.setup(pd, new Parameter("eval.model"));

		return s;
	}

	public static EvaluationGroup testEvalGroup(int numNSP, int numASP,
			int numCust) {
		EvaluationGroup eg = new EvaluationGroup();
		eg.individuals = new ArrayList<Individual>();
		for (int i = 0; i < numNSP; i++)
			eg.individuals.add(new UnitNSPIndividual());
		for (int i = 0; i < numASP; i++)
			eg.individuals.add(new UnitASPIndividual());
		for (int i = 0; i < numCust; i++)
			eg.individuals.add(new RationalConsumer());
		return eg;
	}

}
