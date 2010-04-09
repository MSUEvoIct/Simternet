package simternet.nsp;

import sim.engine.SimState;
import sim.engine.Steppable;
import simternet.Exogenous;

public class Investor implements Steppable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Represents the outstanding balance the NSP owes the investor.
	 */
	protected Double balance;
	protected Double interestRate;
	protected AbstractNetworkProvider nsp;
	
	protected Double payoffRate;
	/**
	 * Represents the total amount borrowed by the NSP.
	 */
	protected Double totalAmountBorrowed;
	/**
	 * Represents the sum of all payments by the NSP back to the Investor.
	 */
	protected Double totalAmountReceived;	
	
	protected Double totalInterestReceived;
	
	public Investor(AbstractNetworkProvider nsp) {
		this(nsp,Exogenous.interestRate,Exogenous.paybackRate);
	}
	
	public Investor(AbstractNetworkProvider nsp, Double interestRate, Double payoffRate) {
		this.nsp = nsp;
		this.interestRate = interestRate;
		this.payoffRate = payoffRate;
		this.balance = new Double(0.0);
		this.totalAmountBorrowed = new Double(0.0);
		this.totalAmountReceived = new Double(0.0);
	}
	
	/**
	 * @param amount  The amount the NSP wishes to finance
	 * @return The actual amount financed.
	 * 
	 * The NSP calls this method when it wants to actually borrow money.
	 */
	public void finance(Double amount) {
		Double maxAvailable = this.getAvailableFinancing();
		if (amount > maxAvailable) 
			throw new RuntimeException("Financing more than available.");
		
		this.totalAmountBorrowed += amount;
		this.balance += amount;
	}
	/**
	 * @return The maximum amount of money this investor is willing to lend
	 * to the NSP at the current time step.
	 */
	public Double getAvailableFinancing() {
//		return 2e20;
		Double liquidAssets = nsp.getLiquidAssets();
		Double revenue = nsp.getOnePeriodRevenue();
		return (2 * liquidAssets) + (2 * revenue) - this.balance;
	}

	public Double getBalance() {
		return balance;
	}
	
	public Double getInterestRate() {
		return interestRate;
	}

	
	/**
	 * @return The amount the NSP owes
	 */
	public Double getPayment() {
		return this.balance * (this.interestRate + this.payoffRate);
	}

	public Double getPayoffRate() {
		return payoffRate;
	}

	public Double getTotalAmountBorrowed() {
		return totalAmountBorrowed;
	}

	public Double getTotalAmountReceived() {
		return totalAmountReceived;
	}

	public void makePayment(Double amount) {
		this.balance -= amount;
		this.totalAmountReceived += amount;
	}

	@Override
	public void step(SimState state) {
		// Charge interest
		this.balance += (this.balance * this.interestRate);
		
		// To do: Foreclose on hopeless/bankrupt NSPs.
		
	}
	
	

}