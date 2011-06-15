package simternet.jung.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public abstract class Inspector extends JFrame {

	protected Object			object;
	protected GUI				owner;
	private static final long	serialVersionUID	= 1L;

	public Inspector(Object object, GUI owner) {
		super(object.toString());

		this.object = object;
		this.owner = owner;

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Inspector.this.frameClosed();
				Inspector.this.dispose();
			}
		});
	}

	protected void frameClosed() {
		this.owner.removeInspector(this.object);
	}

	abstract public void update();

}
