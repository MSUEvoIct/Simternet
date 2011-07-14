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

	public ParameterPanel(String parameter, String defaultValue) {
		this.init(parameter, defaultValue, null);
	}

	public ParameterPanel(String parameter, String defaultValue, String description) {
		this.init(parameter, defaultValue, description);
	}

	public String getParameter() {
		return this.parameter;
	}

	public abstract String getValue();

	public boolean hasChanged() {
		return this.modifiedCheckBox.isSelected();
	}

	protected void init(String parameter, String defaultValue, String description) {
		this.parameter = parameter;
		this.defaultValue = defaultValue;

		if (description != null)
			this.description = description;
		else
			this.description = this.parameter;

		this.modifiedCheckBox = new JCheckBox();
		this.modifiedCheckBox.setSelected(false);

		this.descriptionLabel = new JLabel(this.description);
		Dimension dim = this.descriptionLabel.getPreferredSize();
		dim.width = ParameterPanel.descriptionLabelWidth;
		this.descriptionLabel.setMinimumSize(dim);
		this.descriptionLabel.setPreferredSize(dim);
		this.descriptionLabel.setMaximumSize(dim);

		this.initValueComponent();

		// this.setPreferredSize(new Dimension(500, 30));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.add(this.modifiedCheckBox);
		this.add(this.descriptionLabel);
		this.add(Box.createHorizontalStrut(ParameterPanel.gapWidth));
		this.add(this.valueComponent);

		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		dim = this.getPreferredSize();
		dim.width = 600;
		this.setMinimumSize(dim);
		this.setPreferredSize(dim);
		this.setMaximumSize(dim);
	}

	protected abstract void initValueComponent();

}
