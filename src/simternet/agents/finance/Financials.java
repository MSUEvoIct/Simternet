package simternet.agents.finance;

import java.io.Serializable;

import simternet.engine.Simternet;
import simternet.engine.asyncdata.AsyncUpdate;
import simternet.engine.asyncdata.Temporal;

/**
 * 
 * Tracks financial condition and performance for entities representing business
 * firms. Regularizes and consolidates code otherwise shared for both
 * application and network service providers.
 * 
 * @author kkoning
 * 
 */
public class Financials implements Serializable, AsyncUpdate {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Temporal<Double>	assetsCapital;
	private Temporal<Double>	assetsLiquid;
	private Temporal<Double>	debtBalance;
	private Temporal<Double>	debtInterestRate;
	private Temporal<Double>	debtPayoffRate;
	private Temporal<Double>	depreciationRate;
	private Temporal<Double>	perStepFinancingCost;
	private Temporal<Double>	perStepInvestment;
	private Temporal<Double>	perStepOperationsCost;
	private Temporal<Double>	perStepRevenue;
	private final Simternet		s;
	private Temporal<Double>	totalFinancingCost;
	private Temporal<Double>	totalInvestment;
	private Temporal<Double>	totalOperationsCost;
	private Temporal<Double>	totalRevenue;

	public Financials(Simternet s, Double endowment) {
		this.s = s;
		assetsLiquid = new Temporal<Double>(endowment);

		// Initialize tracking variables with zero values.
		assetsCapital = new Temporal<Double>(0.0);
		debtBalance = new Temporal<Double>(0.0);
		debtInterestRate = new Temporal<Double>(s.config.financeInterestRate);
		debtPayoffRate = new Temporal<Double>(s.config.financePaybackRate);
		perStepFinancingCost = new Temporal<Double>(0.0, 0.0);
		perStepInvestment = new Temporal<Double>(0.0, 0.0);
		perStepOperationsCost = new Temporal<Double>(0.0, 0.0);
		perStepRevenue = new Temporal<Double>(0.0, 0.0);
		totalFinancingCost = new Temporal<Double>(0.0);
		totalInvestment = new Temporal<Double>(0.0);
		totalOperationsCost = new Temporal<Double>(0.0);
		totalRevenue = new Temporal<Double>(0.0);
		depreciationRate = new Temporal<Double>(s.config.financeDepreciationRate);
	}

	/**
	 * @param capitalInvestment
	 *            The amount to capitalize
	 * @return true unless capital is not available
	 */
	public boolean capitalize(Double capitalInvestment) {
		if (capitalInvestment > getAvailableFinancing())
			return false;

		debtBalance.increase(capitalInvestment);
		assetsCapital.increase(capitalInvestment);
		perStepInvestment.increase(capitalInvestment);
		totalInvestment.increase(capitalInvestment);
		return true;
	}

	private void chargeInterest() {
		Double interestRate = debtInterestRate.get();
		Double balance = debtBalance.get();
		Double amount = interestRate * balance;
		debtBalance.increase(amount);
		perStepFinancingCost.increase(amount);
		totalFinancingCost.increase(amount);
	}

	private void depreciateCapital() {
		double depreciation = assetsCapital.get() * depreciationRate.get();
		assetsCapital.reduce(depreciation);
	}

	public void earn(AssetFinance assetFinance, Double amount) {
		// TODO Auto-generated method stub
		this.earn(amount);
	}

	public void earn(Double revenue) {

		if (revenue.isNaN())
			throw new RuntimeException("Cannot Earn NaN");

		if (revenue > 1E17) {
			revenue = 1E17; // suggested breakpoint for debug
		}

		if (revenue < -1E17) {
			revenue = -1E17; // suggested breakpoint for debug
		}

		assetsLiquid.increase(revenue);
		totalRevenue.increase(revenue);
		perStepRevenue.increase(revenue);
	}

	public Double getAssetsCapital() {
		return assetsCapital.get();
	}

	public Double getAssetsLiquid() {
		return assetsLiquid.get();
	}

	/**
	 * This simple implementation at least provides a debt limit proportional to
	 * the firm's assets.
	 * 
	 * @return the amount this firm can borrow.
	 */
	public Double getAvailableFinancing() {
		Double liquidAssets = assetsLiquid.get();
		Double revenue = perStepRevenue.get();
		return 2 * liquidAssets + 2 * revenue - debtBalance.getFuture();
	}

	public Double getDebtBalance() {
		return debtBalance.get();
	}

	public Double getDebtInterestRate() {
		return debtInterestRate.get();
	}

	public Double getDebtPayment() {
		Double interestRate = debtInterestRate.get();
		Double payoffRate = debtPayoffRate.get();
		Double balance = debtBalance.get();
		return balance * (interestRate + payoffRate);
	}

	public Double getDebtPayoffRate() {
		return debtPayoffRate.get();
	}

	public double getDeltaRevenue() {
		return perStepRevenue.getPastDelta();
	}

	public Double getDepreciationRate() {
		return depreciationRate.get();
	}

	public Double getNetWorth() {
		return assetsCapital.get() + assetsLiquid.get() - debtBalance.get();
	}

	public Double getPerStepFinancingCost() {
		return perStepFinancingCost.get();
	}

	public Double getPerStepInvestment() {
		return perStepInvestment.get();
	}

	public Double getPerStepOperationsCost() {
		return perStepOperationsCost.get();
	}

	public Double getPerStepRevenue() {
		return perStepRevenue.get();
	}

	/**
	 * Get current value of assets and present value of future revenue stream
	 * 
	 * TODO: do this much much better
	 * 
	 * @return net present value of enterprise
	 */
	public Double getPresentValue() {
		Double presentValue = getNetWorth();
		Double totalRevenues = totalRevenue.get();
		Double totalExpenses = totalFinancingCost.get() + totalOperationsCost.get() + totalInvestment.get();
		Double averageProfits = (totalRevenues - totalExpenses) / s.schedule.getSteps();
		presentValue += averageProfits * (1 / getDebtInterestRate());
		return presentValue;
	}

	public Double getTotalFinancingCost() {
		return totalFinancingCost.get();
	}

	public Double getTotalInvestment() {
		return totalInvestment.get();
	}

	public Double getTotalOperationsCost() {
		return totalOperationsCost.get();
	}

	public Double getTotalRevenue() {
		return totalRevenue.get();
	}

	public void makeDebtPayment(Double amount) {
		assetsLiquid.reduce(amount);
		debtBalance.reduce(amount);
	}

	private void serviceDebt() {
		Double payment = getDebtPayment();
		Double amountToPay;
		if (payment > assetsLiquid.getFuture()) {
			amountToPay = assetsLiquid.getFuture();
		} else {
			amountToPay = payment;
		}

		makeDebtPayment(amountToPay);
	}

	@Override
	public String toString() {
		return "Cash: " + assetsLiquid.get() + ", Assets: " + assetsCapital.get() + ", Debt/Avail: "
				+ debtBalance.get() + "/" + getAvailableFinancing() + ", NetWorth: " + getNetWorth();
	}

	/*
	 * Remember, all temporal variables must be updated! This list of variables
	 * should include all Temporal objects defined by this class...
	 * 
	 * (non-Javadoc)
	 * 
	 * @see simternet.temporal.AsyncUpdate#update()
	 */
	@Override
	public void update() {
		chargeInterest();
		serviceDebt();
		depreciateCapital();

		assetsCapital.update();
		assetsLiquid.update();
		debtBalance.update();
		debtInterestRate.update();
		debtPayoffRate.update();
		perStepFinancingCost.update();
		perStepInvestment.update();
		perStepOperationsCost.update();
		perStepRevenue.update();
		totalFinancingCost.update();
		totalInvestment.update();
		totalOperationsCost.update();
		totalRevenue.update();
	}

}
