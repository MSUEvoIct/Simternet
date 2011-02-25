package simternet;

import java.io.Serializable;

import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;

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
	private Temporal<Double>	totalFinancingCost;
	private Temporal<Double>	totalInvestment;
	private Temporal<Double>	totalOperationsCost;
	private Temporal<Double>	totalRevenue;

	public Financials(Simternet s, Double endowment) {
		this.assetsLiquid = new Temporal<Double>(endowment);

		// Initialize tracking variables with zero values.
		this.assetsCapital = new Temporal<Double>(0.0);
		this.debtBalance = new Temporal<Double>(0.0);
		this.debtInterestRate = new Temporal<Double>(Double.parseDouble(s.config.getProperty("financial.interestRate")));
		this.debtPayoffRate = new Temporal<Double>(Double.parseDouble(s.config.getProperty("financial.paybackRate")));
		this.perStepFinancingCost = new Temporal<Double>(0.0, 0.0);
		this.perStepInvestment = new Temporal<Double>(0.0, 0.0);
		this.perStepOperationsCost = new Temporal<Double>(0.0, 0.0);
		this.perStepRevenue = new Temporal<Double>(0.0, 0.0);
		this.totalFinancingCost = new Temporal<Double>(0.0);
		this.totalInvestment = new Temporal<Double>(0.0);
		this.totalOperationsCost = new Temporal<Double>(0.0);
		this.totalRevenue = new Temporal<Double>(0.0);
		this.depreciationRate = new Temporal<Double>(Double.parseDouble(s.config
				.getProperty("financial.depreciationRate")));
	}

	/**
	 * @param capitalInvestment
	 *            The amount to capitalize
	 * @return true unless capital is not available
	 */
	public boolean capitalize(Double capitalInvestment) {
		if (capitalInvestment > this.getAvailableFinancing())
			return false;

		this.debtBalance.increment(capitalInvestment);
		this.assetsCapital.increment(capitalInvestment);
		this.perStepInvestment.increment(capitalInvestment);
		this.totalInvestment.increment(capitalInvestment);
		return true;
	}

	private void chargeInterest() {
		Double interestRate = this.debtInterestRate.get();
		Double balance = this.debtBalance.get();
		Double amount = interestRate * balance;
		this.debtBalance.increment(amount);
		this.perStepFinancingCost.increment(amount);
		this.totalFinancingCost.increment(amount);
	}

	private void depreciateCapital() {
		double depreciation = this.assetsCapital.get() * this.depreciationRate.get();
		this.assetsCapital.decrement(depreciation);
	}

	public void earn(Double revenue) {
		this.assetsLiquid.increment(revenue);
		this.totalRevenue.increment(revenue);
		this.perStepRevenue.increment(revenue);
	}

	public Double getAssetsCapital() {
		return this.assetsCapital.get();
	}

	public Double getAssetsLiquid() {
		return this.assetsLiquid.get();
	}

	/**
	 * This simple implementation at least provides a debt limit proportional to
	 * the firm's assets.
	 * 
	 * @return the amount this firm can borrow.
	 */
	public Double getAvailableFinancing() {
		Double liquidAssets = this.assetsLiquid.get();
		Double revenue = this.perStepRevenue.get();
		return (2 * liquidAssets) + (2 * revenue) - this.debtBalance.getFuture();
	}

	public Double getDebtBalance() {
		return this.debtBalance.get();
	}

	public Double getDebtInterestRate() {
		return this.debtInterestRate.get();
	}

	public Double getDebtPayment() {
		Double interestRate = this.debtInterestRate.get();
		Double payoffRate = this.debtPayoffRate.get();
		Double balance = this.debtBalance.get();
		return balance * (interestRate + payoffRate);
	}

	public Double getDebtPayoffRate() {
		return this.debtPayoffRate.get();
	}

	public Double getPerStepFinancingCost() {
		return this.perStepFinancingCost.get();
	}

	public Double getPerStepInvestment() {
		return this.perStepInvestment.get();
	}

	public Double getPerStepOperationsCost() {
		return this.perStepOperationsCost.get();
	}

	public Double getPerStepRevenue() {
		return this.perStepRevenue.get();
	}

	public Double getTotalFinancingCost() {
		return this.totalFinancingCost.get();
	}

	public Double getTotalInvestment() {
		return this.totalInvestment.get();
	}

	public Double getTotalOperationsCost() {
		return this.totalOperationsCost.get();
	}

	public Double getTotalRevenue() {
		return this.totalRevenue.get();
	}

	public void makeDebtPayment(Double amount) {
		this.assetsLiquid.decrement(amount);
		this.debtBalance.decrement(amount);
	}

	private void serviceDebt() {
		Double payment = this.getDebtPayment();
		Double amountToPay;
		if (payment > this.assetsLiquid.getFuture())
			amountToPay = this.assetsLiquid.getFuture();
		else
			amountToPay = payment;

		this.makeDebtPayment(amountToPay);
	}

	@Override
	public String toString() {
		return "Cash: " + this.assetsLiquid.get() + ", Assets: " + this.assetsCapital.get() + ", Debt/Avail: "
				+ this.debtBalance.get() + "/" + this.getAvailableFinancing();
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
		this.chargeInterest();
		this.serviceDebt();
		this.depreciateCapital();

		this.assetsCapital.update();
		this.assetsLiquid.update();
		this.debtBalance.update();
		this.debtInterestRate.update();
		this.debtPayoffRate.update();
		this.perStepFinancingCost.update();
		this.perStepInvestment.update();
		this.perStepOperationsCost.update();
		this.perStepRevenue.update();
		this.totalFinancingCost.update();
		this.totalInvestment.update();
		this.totalOperationsCost.update();
		this.totalRevenue.update();
	}

}
