package simternet;

import java.io.Serializable;

import simternet.temporal.AsyncUpdate;
import simternet.temporal.Temporal;

public class AssetFinance implements AsyncUpdate, Serializable {
	private static final Double	defaultDepreciationRate	= 0.07;
	private static final Double	defaultMaintenanceRate	= 0.03;
	private static final Double	defaultMaintenanceRatio	= 0.5;
	private static final long	serialVersionUID		= 1L;

	Temporal<Double>			accumulatedDepreciation	= new Temporal<Double>(0.0);
	Temporal<Double>			amountInvested			= new Temporal<Double>(0.0);
	final Object				asset;
	Temporal<Double>			capitalBalance			= new Temporal<Double>(0.0);
	Temporal<Double>			depreciationRate		= new Temporal<Double>(AssetFinance.defaultDepreciationRate);
	Temporal<Double>			maintenancePaid			= new Temporal<Double>(0.0);
	Temporal<Double>			maintenanceRate			= new Temporal<Double>(AssetFinance.defaultMaintenanceRate);
	Temporal<Double>			maintenanceRatio		= new Temporal<Double>(AssetFinance.defaultMaintenanceRatio);
	final Financials			owner;
	Temporal<Double>			revenueFrom				= new Temporal<Double>(0.0);

	public AssetFinance(Object asset, Financials owner) {
		this.asset = asset;
		this.owner = owner;
		// owner.registerAsset(this);
	}

	public AssetFinance(Object asset, Financials owner, Double depreciationRate) {
		this(asset, owner);
		this.depreciationRate = new Temporal<Double>(depreciationRate);
	}

	public void depreciate() {
		Double amount = capitalBalance.get() * depreciationRate.get();
		accumulatedDepreciation.increase(amount);
		capitalBalance.reduce(amount);
	}

	public void earn(Double amount) {
		revenueFrom.increase(amount);
		owner.earn(this, amount);
	}

	public void invest(Double amount) {
		amountInvested.increase(amount);
		capitalBalance.increase(amount);
	}

	public void maintain() {
		Double onAmount = (maintenanceRatio.get() * amountInvested.get() + (1 - maintenanceRatio.get())
				* capitalBalance.get()) / 2.0;
		Double amount = onAmount * maintenanceRate.get();
		maintenancePaid.increase(amount);
		// owner.payMaintenance(this);
	}

	public void setDepreciationRate(Double rate) {
		if (rate < 0 || rate > 1)
			throw new RuntimeException("Depreciation rate must be between 0 and 1");
		depreciationRate.set(rate);
	}

	/**
	 * The percentage paid to maintain this asset. For information on the amount
	 * to which this percentage is applied, see setMaintenanceRatio()
	 * 
	 * @param rate
	 */
	public void setMaintenanceRate(Double rate) {
		if (rate < 0 || rate > 1)
			throw new RuntimeException("Maintenance rate must be between 0 and 1");
		maintenanceRate.set(rate);
	}

	/**
	 * The maintenance ratio is used to calculate what balance should be subject
	 * to the maintenance rate. If = 1, the total amount invested in this asset
	 * is used. If = 0, the capital balance (post-depreciation) is used. Other
	 * values weigh these two variables proportionally.
	 * 
	 * @param ratio
	 */
	public void setMaintenanceRatio(Double ratio) {
		if (ratio < 0 || ratio > 1)
			throw new RuntimeException("Maintenance ratio must be between 0 and 1");
		maintenanceRate.set(ratio);
	}

	@Override
	public void update() {
		depreciate();
		maintain();

		accumulatedDepreciation.update();
		amountInvested.update();
		capitalBalance.update();
		depreciationRate.update();
		revenueFrom.update();
		maintenancePaid.update();
		maintenanceRate.update();
		maintenanceRatio.update();
	}

}
