package simternet.launcher;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class StringParameterPanel extends ParameterPanel implements KeyListener {

	private static final long	serialVersionUID	= 1L;

	public StringParameterPanel(String parameter, String defaultValue) {
		super(parameter, defaultValue);
	}

	public StringParameterPanel(String parameter, String defaultValue, String description) {
		super(parameter, defaultValue, description);
	}

	@Override
	public String getValue() {
		return ((JTextField) this.valueComponent).getText();
	}

	@Override
	protected void initValueComponent() {
		this.valueComponent = new JTextField(this.defaultValue);
		((JTextField) this.valueComponent).addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		StringParameterPanel.this.modifiedCheckBox.setSelected(true);
	}
}
