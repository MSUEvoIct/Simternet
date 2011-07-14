package simternet.launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class BoolParameterPanel extends ParameterPanel implements ActionListener {

	private static final long	serialVersionUID	= 1L;

	public BoolParameterPanel(String parameter, boolean defaultValue) {
		super(parameter, defaultValue ? "true" : "false");
	}

	public BoolParameterPanel(String parameter, boolean defaultValue, String description) {
		super(parameter, defaultValue ? "true" : "false", description);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		BoolParameterPanel.this.modifiedCheckBox.setSelected(true);
	}

	@Override
	public String getValue() {
		if (((JCheckBox) this.valueComponent).isSelected())
			return "true";
		else
			return "false";
	}

	@Override
	protected void initValueComponent() {
		this.valueComponent = new JCheckBox();
		if (this.defaultValue == "true")
			((JCheckBox) this.valueComponent).setSelected(true);
		else
			((JCheckBox) this.valueComponent).setSelected(false);
		((JCheckBox) this.valueComponent).addActionListener(this);
	}

}
