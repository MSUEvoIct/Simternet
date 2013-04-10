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

	/**
	 * TODO: Implement this
	 * 
	 * This test function verifies that the Simternet networking code processes
	 * network congestion with the WFQ congestion algorithm correctly. This
	 * process approximates what happens in an actual router implementing
	 * standard TCP/IP congestion control algorithms without specific QoS
	 * prioritization.
	 * 
	 */
	@Test
	public void wfqCongestionTest() {
		/*
		 * Create a collection of NetFlow objects. Each one represents the flow
		 * of an individual user group. Assign them random bandwidths from a
		 * [0..whatever] range.
		 */

		/*
		 * Create a test BackboneLink object. Its capacity should be less than
		 * the sum total of all of the bandwidths in the collection of NetFlow
		 * objects. This means some congestion will be required for all flows to
		 * pass across this link. About 0.75 or so as a ratio would be good.
		 */

		/*
		 * Apply the WFQCongestionAlgorithm, given the collection of flows and
		 * the BackboneLink.
		 */

		/*
		 * Check the result. The flows should now have different bandwidths. If
		 * they are sorted by bandwidth in increasing order, some of them should
		 * start out as NOT congested; i.e., their bandwidth should be the same
		 * as their originally assigned bandwidth.  However, after a certain point,
		 * all remaining flows should be set to the same bandwidth.
		 * 
		 * The sum total of all bandwidths of flows AFTER congestion should be
		 * (at least approximately) equal to the capacity of the BackboneLink
		 * created for this test.  If not, something is wrong.  If we can figure
		 * out how to get it closer, that's great.  Otherwise, if it's within
		 * a small margin, that's probably OK too.
		 */

	}

}
