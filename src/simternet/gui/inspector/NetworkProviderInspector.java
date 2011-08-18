package simternet.gui.inspector;

import simternet.ecj.EvolvableAgent;
import simternet.gui.inspector.property.BooleanProperty;
import simternet.gui.inspector.property.DoubleProperty;
import simternet.gui.inspector.property.IntegerProperty;
import simternet.gui.inspector.property.StringProperty;
import simternet.gui.inspector.property.TreeProperty;
import simternet.nsp.NetworkProvider;

/**
 * Inspects NetworkProvider objects
 * 
 * Currently displays no information. TODO: Add information to display
 * 
 * @author graysonwright
 * 
 */
public class NetworkProviderInspector extends Inspector {

	protected StringProperty	name;
	protected NetworkProvider	nsp;
	protected DoubleProperty	fitness, capitalAssets, liquidAssets, totalInvestment, totalFinancing, totalOperating,
			totalRevenue, numCustomers, marketShare;
	protected IntegerProperty	numEdges;
	protected BooleanProperty	bankrupt;
	protected TreeProperty		trees;
	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param nsp
	 *            object to inspect
	 */
	public NetworkProviderInspector(NetworkProvider nsp) {
		super(nsp.s, nsp.toString());
		this.nsp = nsp;

		name = new StringProperty("Name", nsp.getName(), sim);
		this.add(name);

		fitness = new DoubleProperty("Fitness", sim);
		this.add(fitness);

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

		numEdges = new IntegerProperty("Number of Edges", sim);
		this.add(numEdges);

		bankrupt = new BooleanProperty("Bankrupt?", sim);
		this.add(bankrupt);

		numCustomers = new DoubleProperty("Number of Customers", sim);
		this.add(numCustomers);

		marketShare = new DoubleProperty("Market Share", sim);
		this.add(marketShare);

		if (this.nsp instanceof EvolvableAgent) {
			trees = new TreeProperty("ECJ Trees", (EvolvableAgent) nsp);
			this.add(trees);
		}
		this.update();
	}

	/**
	 * Updates the displayed values with the current values in the Simternet
	 * simulation.
	 */
	@Override
	public void update() {
		name.setValue(nsp.getName());

		fitness.setValue(nsp.financials.getNetWorth());
		capitalAssets.setValue(nsp.financials.getAssetsCapital());
		liquidAssets.setValue(nsp.financials.getAssetsLiquid());

		totalInvestment.setValue(nsp.financials.getTotalInvestment());
		totalFinancing.setValue(nsp.financials.getTotalFinancingCost());
		totalOperating.setValue(nsp.financials.getTotalOperationsCost());
		totalRevenue.setValue(nsp.financials.getTotalRevenue());

		numEdges.setValue(nsp.getEdgeNetworks().size());
		bankrupt.setValue(nsp.bankrupt);
		numCustomers.setValue(nsp.getCustomers());
		marketShare.setValue(nsp.getCustomers() / sim.getPopulation());

	}
}
