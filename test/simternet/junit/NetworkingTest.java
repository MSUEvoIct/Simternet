package simternet.junit;

import org.junit.Test;

import simternet.Simternet;
import ec.agency.eval.EvaluationGroup;

public class NetworkingTest {

	@Test
	public void test() {
		Simternet s = SimternetTest.basicSimternet();
		EvaluationGroup eg = SimternetTest.testEvalGroup(1, 1, 1);
		s.setEvaluationGroup(eg);
		s.run();
	}

}
