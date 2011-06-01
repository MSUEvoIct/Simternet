package simternet.jung.filter;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public abstract class SingleFilter<V, E> extends EasyFilter<V, E> {

	@Override
	public Enumeration<EasyFilter<V, E>> children() {
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
}
