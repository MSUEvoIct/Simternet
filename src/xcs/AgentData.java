package xcs;

import xcs.util.RandomNumber;
import fuzzy.EvaluationException;
import fuzzy.FuzzyBlockOfRules;
import fuzzy.FuzzyEngine;
import fuzzy.LinguisticVariable;
import fuzzy.NoRulesFiredException;
import fuzzy.RulesParsingException;

public class AgentData {

	public static final int ANTECEDENT_LENGTH = 3;
	public static final int CONSEQUENT_LENGTH = 1;

	private LinguisticVariable deltaRevenue = null;
	private LinguisticVariable spend = null;

	public AgentData() {
		this.generateRandomData();
	}

	public AgentData(AgentData agentData) {
		this.deltaRevenue.setInputValue(agentData.getDeltaRevenue());
	}

	public AgentData(Double rev) {
		this.setDeltaRevenue(rev);
	}

	public String createRuleBlock(String bin) {
		String rules = "if";
		if (bin.charAt(0) == '1')
			rules = rules + " revenue is poor or";
		if (bin.charAt(1) == '1')
			rules = rules + " revenue is well or";
		if (bin.charAt(2) == '1')
			rules = rules + " revenue is rich or";
		rules = rules.substring(0, rules.length() - 2);
		rules = rules + "then spend is ";
		if (bin.charAt(2) == '0')
			rules = rules + "little";
		else
			rules = rules + "lots";
		return rules;
	}

	public double defuzzify(String rules) {
		// Should be optimized at some point

		// if the antecedent doesn't contain a 1, no rules exist so return 0.0
		if (!rules.substring(0, rules.length() - AgentData.CONSEQUENT_LENGTH)
				.contains("1"))
			return 0.0;
		if (this.spend == null) {
			this.spend = new LinguisticVariable("spend");
			this.spend.add("little", 0, 5, 5, 5);
			this.spend.add("moderate", 5, 10, 10, 10);
			this.spend.add("lots", 10, 15, 15, 15);
		}

		// 2. Create a fuzzy engine
		FuzzyEngine fuzzyEngine = new FuzzyEngine();

		// 3. Register all LVs
		fuzzyEngine.register(this.deltaRevenue);
		fuzzyEngine.register(this.spend);

		// 4. Create a block of rules
		FuzzyBlockOfRules fuzzyBlockOfRules = new FuzzyBlockOfRules(this
				.createRuleBlock(rules));

		// 5. Register the block
		fuzzyEngine.register(fuzzyBlockOfRules);

		// 6. Parse the rules
		try {
			fuzzyBlockOfRules.parseBlock();
		} catch (RulesParsingException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// 7. Perform the evaluation
		try {
			fuzzyBlockOfRules.evaluateBlock();
		} catch (EvaluationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		double ret = 0.0;
		try {
			ret = this.spend.defuzzify();
		} catch (NoRulesFiredException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return ret;
	}

	public void generateRandomData() {
		this.setDeltaRevenue(20000 * RandomNumber.getDouble());
	}

	public String getBinaryString() {
		String ret = "";
		if (this.deltaRevenue.is("poor") > 0.0)
			ret = ret + "1";
		else
			ret = ret + "0";
		if (this.deltaRevenue.is("well") > 0.0)
			ret = ret + "1";
		else
			ret = ret + "0";
		if (this.deltaRevenue.is("rich") > 0.0)
			ret = ret + "1";
		else
			ret = ret + "0";
		return ret;
	}

	public double getDeltaRevenue() {
		return this.deltaRevenue.getInputValue();
	}

	public boolean isMatching(String rules) {
		// Should be optimized at some point
		if (!rules.substring(0, rules.length() - 1).contains("1"))
			return false;
		if (this.spend == null) {
			this.spend = new LinguisticVariable("spend");
			this.spend.add("little", 0, 5, 5, 5);
			this.spend.add("moderate", 5, 10, 10, 10);
			this.spend.add("lots", 10, 15, 15, 15);
		}

		// 2. Create a fuzzy engine
		FuzzyEngine fuzzyEngine = new FuzzyEngine();

		// 3. Register all LVs
		fuzzyEngine.register(this.deltaRevenue);
		fuzzyEngine.register(this.spend);

		// 4. Create a block of rules
		FuzzyBlockOfRules fuzzyBlockOfRules = new FuzzyBlockOfRules(this
				.createRuleBlock(rules));

		// 5. Register the block
		fuzzyEngine.register(fuzzyBlockOfRules);

		// 6. Parse the rules
		try {
			fuzzyBlockOfRules.parseBlock();
		} catch (RulesParsingException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// 7. Perform the evaluation
		try {
			fuzzyBlockOfRules.evaluateBlock();
		} catch (EvaluationException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return fuzzyBlockOfRules.isRuleFired();
	}

	public void setDeltaRevenue(double rev) {
		if (this.deltaRevenue == null) {
			this.deltaRevenue = new LinguisticVariable("revenue");
			this.deltaRevenue.add("poor", 0, 0, 3000, 5000);
			this.deltaRevenue.add("well", 3000, 5000, 8000, 11000);
			this.deltaRevenue.add("rich", 8000, 11000, 20000, 20000);
		}
		this.deltaRevenue.setInputValue(rev);
	}

}
