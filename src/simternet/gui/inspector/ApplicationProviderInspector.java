package simternet.gui.inspector;

import simternet.agents.asp.ApplicationProvider;
import simternet.ecj.EvolvableAgent;
import simternet.gui.inspector.property.DoubleProperty;
import simternet.gui.inspector.property.StringProperty;
import simternet.gui.inspector.property.TreeProperty;

/**
 * Inspects ApplicationProvider objects
 * 
 * displays selected information about an individual ApplicationProvider
 * 
 * @author graysonwright
 */
public class ApplicationProviderInspector extends Inspector {

	protected ApplicationProvider	asp;
	protected StringProperty		category;
	protected DoubleProperty		quality, price, fitness, bandwidth, numCustomers, capitalAssets, liquidAssets,
			totalInvestment, totalFinancing, totalOperating, totalRevenue;
	protected TreeProperty			trees;
	private static final long		serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param asp
	 *            the object to be inspected
	 */
	public ApplicationProviderInspector(ApplicationProvider asp) {
		super(asp.s, asp.toString());
		this.asp = asp;

		category = new StringProperty("Category", asp.getAppCategoryString(), sim);
		this.add(category);

		quality = new DoubleProperty("Quality", asp.getQuality(), sim);
		this.add(quality);

		price = new DoubleProperty("Subscription Price", asp.getPriceSubscriptions(), sim);
		this.add(price);

		fitness = new DoubleProperty("Fitness", sim);
		this.add(fitness);

		bandwidth = new DoubleProperty("Bandwidth", sim);
		this.add(bandwidth);

		capitalAssets = new DoubleProperty("Capital Assets", sim);
		this.add(capitalAssets);

		liquidAssets = new DoubleProperty("Liquid Assets", sim);
		this.add(liquidAssets);

		totalInvestment = new DoubleProperty("Total Investment", sim);
		this.add(totalInvestment);

		totalFinancing = new DoubleProperty("Total Financing", sim);
		this.add(totalFinancing);

		totalOperating = new DoubleProperty("Total Operating", sim);
		this.add(totalOperating);

		totalRevenue = new DoubleProperty("Total Revenue", sim);
		this.add(totalRevenue);

		numCustomers = new DoubleProperty("Number of Customers", sim);
		this.add(numCustomers);

		if (this.asp instanceof EvolvableAgent) {
			trees = new TreeProperty("ECJ Trees", (EvolvableAgent) this.asp);
			this.add(trees);
		}

		this.update();
	}

	/**
	 * Updates the information displayed to reflect recent changes in the
	 * simulation
	 */
	@Override
	public void update() {
		category.setValue(asp.getAppCategoryString());
		quality.setValue(asp.getQuality());
		price.setValue(asp.getPriceSubscriptions());

		fitness.setValue(asp.getFinancials().getNetWorth());
		capitalAssets.setValue(asp.getFinancials().getAssetsCapital());
		liquidAssets.setValue(asp.getFinancials().getAssetsLiquid());

		totalInvestment.setValue(asp.getFinancials().getTotalInvestment());
		totalFinancing.setValue(asp.getFinancials().getTotalFinancingCost());
		totalOperating.setValue(asp.getFinancials().getTotalOperationsCost());
		totalRevenue.setValue(asp.getFinancials().getTotalRevenue());

		numCustomers.setValue(asp.getCustomers());
	}

}
