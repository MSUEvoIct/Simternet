package simternet.launcher;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IntParameterPanel extends ParameterPanel implements ChangeListener {

	private static final long	serialVersionUID	= 1L;

	public IntParameterPanel(String parameter, int defaultValue) {
		super(parameter, Integer.toString(defaultValue));
	}

	public IntParameterPanel(String parameter, int defaultValue, String description) {
		super(parameter, Integer.toString(defaultValue), description);
	}

	@Override
	public String getValue() {
		Integer value = (Integer) ((JSpinner) valueComponent).getValue();
		return value.toString();
	}

	@Override
	protected void initValueComponent() {
		valueComponent = new JSpinner(new SpinnerNumberModel(Integer.parseInt(defaultValue), 1, 400, 1));
		((JSpinner) valueComponent).addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		IntParameterPanel.this.modifiedCheckBox.setSelected(true);
	}

}
