package simternet.jung.filter;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

/**
 * A single EasyFilter (non-composite). Compatible with JTreeChart, which is
 * used to turn filters on and off in the graphical interface.
 * 
 * @author graysonwright
 * 
 * @param <V>
 *            vertex type
 * @param <E>
 *            edge type
 */
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
