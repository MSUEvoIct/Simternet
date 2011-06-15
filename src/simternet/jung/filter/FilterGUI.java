package simternet.jung.filter;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel.CheckingMode;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import simternet.jung.gui.GUI;

public class FilterGUI extends JFrame implements TreeCheckingListener {

	private GUI				owner;
	private JPanel				panel;
	private TreeNode			root;
	private CheckboxTree		tree;
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// public FilterGUI() {
	// super("Available Filters");
	// this.init();
	// }

	public FilterGUI(GUI owner, TreeNode r) {
		super("Available Filters");

		this.init(owner, r);
	}

	private void init(GUI owner, TreeNode r) {

		this.owner = owner;

		this.panel = new JPanel();

		this.setContentPane(this.panel);
		this.panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.root = r;
		this.setTree(new CheckboxTree(this.root));

		this.tree.getCheckingModel().setCheckingMode(CheckingMode.PROPAGATE_PRESERVING_UNCHECK);

		this.tree.addTreeCheckingListener(this);

		this.panel.add(this.tree);
		this.pack();
	}

	public void setTree(CheckboxTree tree) {
		this.tree = tree;
	}

	public void valueChanged(TreeCheckingEvent e) {
		TreePath[] paths = this.tree.getCheckingPaths();
		this.owner.updateFilters(paths);
	}
}
