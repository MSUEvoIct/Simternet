package fuzzy;

import xcs.AgentData;

public class SandBox {

	public static void main(String args[]) throws RulesParsingException,
			EvaluationException, NoRulesFiredException {
		AgentData ad = new AgentData(5000.0);

		System.out.println(ad.isMatching("0111"));

		LinguisticVariable assets = new LinguisticVariable("assets");
		assets.add("poor", 0, 0, 3000, 5000);
		assets.add("well", 3000, 5000, 8000, 11000);
		assets.add("rich", 8000, 11000, 20000, 20000);
		assets.setInputValue(11000);

		LinguisticVariable spend = new LinguisticVariable("spend");
		spend.add("little", 0, 5, 5, 5);
		spend.add("moderate", 5, 10, 10, 10);
		spend.add("lots", 10, 15, 15, 15);
		// 2. Create a fuzzy engine
		FuzzyEngine fuzzyEngine = new FuzzyEngine();

		// 3. Register all LVs
		fuzzyEngine.register(assets);
		fuzzyEngine.register(spend);

		// 4. Create a block of rules
		FuzzyBlockOfRules fuzzyBlockOfRules = new FuzzyBlockOfRules(
				"if assets is poor then spend is little\nif assets is well then spend is moderate\nif assets is rich then spend is lots");

		// 5. Register the block
		fuzzyEngine.register(fuzzyBlockOfRules);

		// 6. Parse the rules
		fuzzyBlockOfRules.parseBlock();

		// 7. Perform the evaluation
		fuzzyBlockOfRules.evaluateBlock();// - faster execution
		System.out.println(fuzzyBlockOfRules.evaluateBlockText());// - slower
		// execution,
		// returns a
		// String
		// with
		// evaluation
		// results
		// for every
		// fuzzy
		// expression

		// 8. Obtain the result(s)
		double result = spend.defuzzify();
		System.out.println(result);
	}
}
