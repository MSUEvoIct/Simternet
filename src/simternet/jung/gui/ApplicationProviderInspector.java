package simternet.jung.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;

import simternet.application.ApplicationProvider;

/**
 * Inspects ApplicationProvider objects
 * 
 * Currently displays the category, quality, and price of an ApplicationProvider
 * 
 * @author graysonwright
 */
public class ApplicationProviderInspector extends Inspector {

	protected JLabel			categoryLabel, qualityLabel, priceLabel;
	protected static final int	numRows				= 3;
	private static final long	serialVersionUID	= 1L;

	/**
	 * Initializes the object and defines the layout
	 * 
	 * @param asp
	 *            the object to be inspected
	 * @param owner
	 *            the GUI to which this inspector reports
	 */
	public ApplicationProviderInspector(ApplicationProvider asp, GUI owner) {
		super(asp, owner);

		this.setLayout(new GridLayout(EdgeInspector.numRows, 2, 20, 5));

		this.categoryLabel = new JLabel();
		this.qualityLabel = new JLabel();
		this.priceLabel = new JLabel();

		this.add(new JLabel("Category"));
		this.add(this.categoryLabel);

		this.add(new JLabel("Quality"));
		this.add(this.qualityLabel);

		this.add(new JLabel("Subscription Price"));
		this.add(this.priceLabel);

		this.update();
	}

	/**
	 * Updates the information displayed to reflect recent changes in the
	 * simulation
	 */
	@Override
	public void update() {

		ApplicationProvider asp = (ApplicationProvider) this.object;

		String categoryString;
		switch (asp.getAppCategory()) {
		case COMMUNICATION:
			categoryString = "Communication";
			break;
		case ENTERTAINMENT:
			categoryString = "Entertainment";
			break;
		case INFORMATION:
			categoryString = "Information";
			break;
		default:
			categoryString = "Undefined";
			break;
		}
		this.categoryLabel.setText(categoryString);

		this.qualityLabel.setText(asp.getQuality().toString());

		this.priceLabel.setText(asp.getPriceSubscriptions().toString());
	}

}
