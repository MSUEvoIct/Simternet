package simternet.gui.inspector;

import simternet.application.ApplicationProvider;
import simternet.ecj.EvolvableAgent;
import simternet.gui.GUI;
import simternet.gui.inspector.property.DoubleProperty;
import simternet.gui.inspector.property.StringProperty;
import simternet.gui.inspector.property.TreeProperty;

/**
 * Inspects ApplicationProvider objects
 * 
 * Currently displays the category, quality, and price of an ApplicationProvider
 * 
 * @author graysonwright
 */
public class ApplicationProviderInspector extends Inspector {

	protected ApplicationProvider	asp;
	protected StringProperty		category;
	protected DoubleProperty		quality, price;
	protected TreeProperty			trees;
	private static final long		serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param asp
	 *            the object to be inspected
	 * @param owner
	 *            the GUI to which this inspector reports
	 */
	public ApplicationProviderInspector(ApplicationProvider asp, GUI owner) {
		super(asp.toString(), owner);
		this.asp = asp;

		category = new StringProperty("Category", asp.getAppCategoryString(), sim);
		this.add(category);

		quality = new DoubleProperty("Quality", asp.getQuality(), sim);
		this.add(quality);

		price = new DoubleProperty("Subscription Price", asp.getPriceSubscriptions(), sim);
		this.add(price);

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
	}

}
