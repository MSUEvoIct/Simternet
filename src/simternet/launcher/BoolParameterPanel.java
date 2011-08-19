package simternet.launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/**
 * A parameter panel that displays and accepts input for a boolean paramter
 * 
 * @author graysonwright
 * 
 */
public class BoolParameterPanel extends ParameterPanel implements ActionListener {

	private static final long	serialVersionUID	= 1L;

	public BoolParameterPanel(String parameter, boolean defaultValue) {
		super(parameter, defaultValue ? "true" : "false");
	}

	public BoolParameterPanel(String parameter, boolean defaultValue, String description) {
		super(parameter, defaultValue ? "true" : "false", description);
	}

	/**
	 * Called when the user checks the checkbox.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		BoolParameterPanel.this.modifiedCheckBox.setSelected(true);
	}

	@Override
	public String getValue() {
		if (((JCheckBox) valueComponent).isSelected())
			return "true";
		else
			return "false";
	}

	/**
	 * Our valueComponent is a checkbox, representing a boolean value
	 */
	@Override
	protected void initValueComponent() {
		valueComponent = new JCheckBox();
		if (defaultValue == "true") {
			((JCheckBox) valueComponent).setSelected(true);
		} else {
			((JCheckBox) valueComponent).setSelected(false);
		}
		((JCheckBox) valueComponent).addActionListener(this);
	}

}
