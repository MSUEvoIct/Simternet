package sandbox;

import javax.swing.JFrame;

import ec.util.MersenneTwisterFast;

public class test {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// try {
		// test window = new test();
		// window.frame.setVisible(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });

		MersenneTwisterFast mtf = new MersenneTwisterFast(4000);
		for (int i = 0; i < 10; i++)
			System.out.println(i + " = " + mtf.nextInt());

		mtf = new MersenneTwisterFast(-1465451779);
		for (int i = 0; i < 10; i++)
			System.out.println(i + " = " + mtf.nextInt());

	}

	private JFrame	frame;

	/**
	 * Create the application.
	 */
	public test() {
		this.initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.frame = new JFrame();
		this.frame.setBounds(100, 100, 450, 300);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
