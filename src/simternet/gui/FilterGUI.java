package simternet.gui;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel.CheckingMode;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * A GUI that allows the user to select which filters are currently enabled, and
 * which are not. Does not allow the user to create new filters. Filters need to
 * be hard-coded in GUI.java
 * 
 * @author graysonwright
 * 
 */
public class FilterGUI extends JFrame implements TreeCheckingListener {

	private GUI					owner;
	private JPanel				panel;
	private TreeNode			root;
	private CheckboxTree		tree;

	private static final long	serialVersionUID	= 1L;

	public FilterGUI(GUI owner, TreeNode r) {
		super("Available Filters");

		init(owner, r);
	}

	/**
	 * Initializes and defines layout (a tree structure)
	 * 
	 * @param owner
	 *            the GUI that this JFrame reports to
	 * @param root
	 *            the root of the filter tree
	 */
	private void init(GUI owner, TreeNode root) {
		this.owner = owner;

		panel = new JPanel();
		setContentPane(panel);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.root = root;
		setTree(new CheckboxTree(this.root));

		tree.getCheckingModel().setCheckingMode(CheckingMode.PROPAGATE_PRESERVING_UNCHECK);
		tree.addTreeCheckingListener(this);

		panel.add(tree);
		pack();
	}

	public void setTree(CheckboxTree tree) {
		this.tree = tree;
	}

	/**
	 * Called by the gui when a node is selected or de-selected. Notifies the
	 * owner that changes were made
	 */
	public void valueChanged(TreeCheckingEvent e) {
		TreePath[] paths = tree.getCheckingPaths();
		owner.updateFilters(paths);
	}
}
