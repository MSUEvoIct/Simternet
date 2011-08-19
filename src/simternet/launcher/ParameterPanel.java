package simternet.launcher;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * A ParameterPanel displays the name and current value of a runtime parameter
 * onscreen, allowing the user to change it before it gets passed on to ECJ
 * 
 * @author graysonwright
 * 
 */
public abstract class ParameterPanel extends JPanel {

	// TODO: add a restriction field for the values.

	protected String			defaultValue;
	protected String			description;
	protected JLabel			descriptionLabel;
	protected JCheckBox			modifiedCheckBox;
	protected String			parameter;

	protected JComponent		valueComponent;

	protected static final int	descriptionLabelWidth	= 300;

	protected static final int	gapWidth				= 50;
	private static final long	serialVersionUID		= 1L;

	/**
	 * Creates a ParameterPanel
	 * 
	 * @param parameter
	 *            the name of the parameter
	 * @param defaultValue
	 *            the initial value of the parameter in the config file
	 */
	public ParameterPanel(String parameter, String defaultValue) {
		init(parameter, defaultValue, null);
	}

	/**
	 * Creates a ParameterPanel
	 * 
	 * @param parameter
	 *            the name of the parameter
	 * @param defaultValue
	 *            the initial value of the parameter in the config file
	 * @param description
	 *            the user-friendly description to display
	 */
	public ParameterPanel(String parameter, String defaultValue, String description) {
		init(parameter, defaultValue, description);
	}

	public String getParameter() {
		return parameter;
	}

	public abstract String getValue();

	public boolean hasChanged() {
		return modifiedCheckBox.isSelected();
	}

	/**
	 * initializes the parameterPanel, setting its dimensions and laying out its
	 * components
	 * 
	 * @param parameter
	 *            the name of the parameter
	 * @param defaultValue
	 *            the initial value of the parameter, taken from the config file
	 * @param description
	 *            the user-friendly description to display
	 */
	protected void init(String parameter, String defaultValue, String description) {
		this.parameter = parameter;
		this.defaultValue = defaultValue;

		if (description != null) {
			this.description = description;
		} else {
			this.description = this.parameter;
		}

		modifiedCheckBox = new JCheckBox();
		modifiedCheckBox.setSelected(false);

		descriptionLabel = new JLabel(this.description);
		Dimension dim = descriptionLabel.getPreferredSize();
		dim.width = ParameterPanel.descriptionLabelWidth;
		descriptionLabel.setMinimumSize(dim);
		descriptionLabel.setPreferredSize(dim);
		descriptionLabel.setMaximumSize(dim);

		initValueComponent();

		// this.setPreferredSize(new Dimension(500, 30));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.add(modifiedCheckBox);
		this.add(descriptionLabel);
		this.add(Box.createHorizontalStrut(ParameterPanel.gapWidth));
		this.add(valueComponent);

		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		dim = getPreferredSize();
		dim.width = 600;
		setMinimumSize(dim);
		setPreferredSize(dim);
		setMaximumSize(dim);
	}

	protected abstract void initValueComponent();

}
