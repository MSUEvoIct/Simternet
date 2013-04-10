package simternet;

import java.io.Serializable;

/**
 * 
 * Tracks financial condition and performance for entities representing business
 * firms. Regularizes and consolidates code otherwise shared for both
 * application and network service providers.
 * 
 * @author kkoning
 * 
 */
public class Financials implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final double unreasonableXactionSize = 1E10;

	double totalInvested;

	double operatingRevenue;
	double operatingExpenses;

	public Financials() {
	}

	public void invest(double amount) {
		sanityCheck(amount);
		totalInvested += amount;
	}

	public void earnRevenue(double amount) {
		sanityCheck(amount);
		operatingRevenue += amount;
	}

	public void payExpense(double amount) {
		sanityCheck(amount);
		operatingExpenses -= amount;
	}

	public double getBalance() {
		double amount = operatingRevenue - operatingExpenses - totalInvested;
		return amount;
	}

	/**
	 * Amount must be non-negative and below an unreasonable size.
	 * 
	 * @param amount
	 */
	private void sanityCheck(double amount) {
		if (Double.isNaN(amount))
			throw new RuntimeException("$NaN seen");

		if (Double.isInfinite(amount))
			throw new RuntimeException("$Infinite seen");

		if (amount < 0)
			throw new RuntimeException("No $ < 0");

		if (amount >= unreasonableXactionSize)
			throw new RuntimeException("Transaction > "
					+ unreasonableXactionSize + " in one xaction");
	}

}
