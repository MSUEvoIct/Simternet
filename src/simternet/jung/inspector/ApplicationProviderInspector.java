package simternet.jung.inspector;

import simternet.application.ApplicationProvider;
import simternet.ecj.EvolvableAgent;
import simternet.jung.gui.GUI;
import simternet.jung.inspector.property.DoubleProperty;
import simternet.jung.inspector.property.StringProperty;
import simternet.jung.inspector.property.TreeProperty;

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

		this.category = new StringProperty("Category", asp.getAppCategoryString());
		this.add(this.category);

		this.quality = new DoubleProperty("Quality", asp.getQuality());
		this.add(this.quality);

		this.price = new DoubleProperty("Subscription Price", asp.getPriceSubscriptions());
		this.add(this.price);

		if (this.asp instanceof EvolvableAgent) {
			this.trees = new TreeProperty("ECJ Trees", (EvolvableAgent) this.asp);
			this.add(this.trees);
		}

		this.update();
	}

	/**
	 * Updates the information displayed to reflect recent changes in the
	 * simulation
	 */
	@Override
	public void update() {
		this.category.setValue(this.asp.getAppCategoryString());
		this.quality.setValue(this.asp.getQuality());
		this.price.setValue(this.asp.getPriceSubscriptions());
	}

}
