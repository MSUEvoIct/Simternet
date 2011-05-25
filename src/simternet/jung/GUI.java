package simternet.jung;

import javax.swing.JPanel;

import simternet.SimternetWithJung;
import simternet.network.BackboneLink;
import simternet.network.Network;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GUI.java
 *
 * Created on May 19, 2011, 1:52:53 PM
 *
 * NOTE!
 * 		This class was almost entirely created using NetBeans IDE. Almost all of the code was automatically generated.
 * 
 * The methods that I wrote are:
 *      GUI - constructor
 *      resetButtonActionPerformed
 *      setSeedLabel
 *      setStepLabel
 *      startButtonActionPerformed
 *      stepButtonActionPerformed
 *      setViewPanel
 *      
 * Everything else defines the interface.
 */
/**
 * 
 * @author graysonwright
 */
public class GUI extends javax.swing.JFrame {

	// Variables declaration - do not modify
	private javax.swing.JPanel		ControlPanel;

	private javax.swing.JButton		filterButton;

	private javax.swing.JSeparator	horSeparator;

	private SimternetWithJung		owner;

	private javax.swing.JButton		resetButton;

	private javax.swing.JComboBox	resetSelector;

	private javax.swing.JLabel		seedLabel;

	private javax.swing.JLabel		seedTextLabel;

	private javax.swing.JButton		startButton;

	private javax.swing.JButton		stepButton;

	private javax.swing.JLabel		stepLabel;
	private javax.swing.JSpinner	stepSelector;
	private javax.swing.JLabel		stepTextLabel;
	private javax.swing.JPanel		viewPanel;

	// End of variables declaration
	/**
	 * Creates new form GUI
	 * 
	 * @param visualizationViewer
	 */
	public GUI(SimternetWithJung s, VisualizationViewer<Network, BackboneLink> visualizationViewer) {
		this.owner = s;
		this.viewPanel = visualizationViewer;
		this.initComponents();
	}

	private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		// this.viewPanel = new javax.swing.JPanel();
		this.ControlPanel = new javax.swing.JPanel();
		this.horSeparator = new javax.swing.JSeparator();
		this.stepTextLabel = new javax.swing.JLabel();
		this.seedTextLabel = new javax.swing.JLabel();
		this.seedLabel = new javax.swing.JLabel();
		this.stepLabel = new javax.swing.JLabel();
		this.startButton = new javax.swing.JButton();
		this.stepButton = new javax.swing.JButton();
		this.stepSelector = new javax.swing.JSpinner();
		this.resetButton = new javax.swing.JButton();
		this.resetSelector = new javax.swing.JComboBox();
		this.filterButton = new javax.swing.JButton();

		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		// this.viewPanel.setBackground(new java.awt.Color(255, 255, 255));
		this.viewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		this.viewPanel.setPreferredSize(new java.awt.Dimension(1000, 1000));

		javax.swing.GroupLayout viewPanelLayout = new javax.swing.GroupLayout(this.viewPanel);
		this.viewPanel.setLayout(viewPanelLayout);
		viewPanelLayout.setHorizontalGroup(viewPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 414, Short.MAX_VALUE));
		viewPanelLayout.setVerticalGroup(viewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 447, Short.MAX_VALUE));

		this.ControlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		this.stepTextLabel.setText("Steps:");

		this.seedTextLabel.setText("Seed:");

		this.seedLabel.setText("0");

		this.stepLabel.setText("0");

		this.startButton.setText("Start");
		this.startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				GUI.this.startButtonActionPerformed(evt);
			}
		});

		this.stepButton.setText("Step:");
		this.stepButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				GUI.this.stepButtonActionPerformed(evt);
			}
		});

		this.resetButton.setText("Reset:");
		this.resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				GUI.this.resetButtonActionPerformed(evt);
			}
		});

		this.resetSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Same Seed", "New Seed" }));

		this.filterButton.setText("Filter Options");
		this.filterButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				GUI.this.filterButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout ControlPanelLayout = new javax.swing.GroupLayout(this.ControlPanel);
		this.ControlPanel.setLayout(ControlPanelLayout);
		ControlPanelLayout
				.setHorizontalGroup(ControlPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								ControlPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												ControlPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING, false)
														.addComponent(this.horSeparator,
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(this.startButton,
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																ControlPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				ControlPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(this.stepButton)
																						.addComponent(this.resetButton))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				ControlPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								false)
																						.addComponent(
																								this.resetSelector,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								0, 0, Short.MAX_VALUE)
																						.addComponent(
																								this.stepSelector,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								120,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																ControlPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				ControlPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								this.seedTextLabel)
																						.addComponent(
																								this.stepTextLabel))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addGroup(
																				ControlPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(this.stepLabel)
																						.addComponent(this.seedLabel)))
														.addComponent(this.filterButton,
																javax.swing.GroupLayout.Alignment.LEADING))
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		ControlPanelLayout.setVerticalGroup(ControlPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				ControlPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.startButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								ControlPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(this.stepButton)
										.addComponent(this.stepSelector, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								ControlPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(this.resetButton)
										.addComponent(this.resetSelector, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
						.addComponent(this.filterButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.horSeparator, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								ControlPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addGroup(
												ControlPanelLayout
														.createSequentialGroup()
														.addComponent(this.seedTextLabel)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(this.stepTextLabel))
										.addGroup(
												ControlPanelLayout
														.createSequentialGroup()
														.addComponent(this.seedLabel)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(this.stepLabel))).addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.viewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(this.ControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(this.viewPanel, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
										.addComponent(this.ControlPanel, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));

		this.pack();
	}// </editor-fold>

	private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
		switch (this.resetSelector.getSelectedIndex()) {
		case 0:
			this.owner.resetSameSeed();
			break;
		case 1:
			this.owner.resetNewSeed();
			break;
		}
	}

	public void setSeedLabel(long s) {
		this.seedLabel.setText(String.valueOf(s));
	}

	public void setStepLabel(int s) {
		this.stepLabel.setText(String.valueOf(s));
		this.repaint();
	}

	public void setViewPanel(JPanel vp) {
		this.viewPanel.removeAll();
		vp.setSize(this.viewPanel.getSize());
		this.viewPanel.add(vp);
		System.out.println("Added a view panel...");
	}

	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.owner.start();
	}

	private void stepButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int n = ((Integer) this.stepSelector.getValue()).intValue();
		this.owner.step(n);
	}
}