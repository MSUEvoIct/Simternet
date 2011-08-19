package simternet.launcher;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/**
 * A parameter panel that displays and accepts input for a String parameter
 * 
 * @author graysonwright
 * 
 */
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
		return ((JTextField) valueComponent).getText();
	}

	/**
	 * valueComponent is a JTextField, allowing the user to input a string.
	 */
	@Override
	protected void initValueComponent() {
		valueComponent = new JTextField(defaultValue);
		((JTextField) valueComponent).addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	/**
	 * When the user types something, we check the modified box, so the change
	 * gets included in the runtime arguments
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		StringParameterPanel.this.modifiedCheckBox.setSelected(true);
	}
}
