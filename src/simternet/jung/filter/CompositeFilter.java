package simternet.jung.filter;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

public class CompositeFilter<V, E> extends EasyFilter<V, E> {

	private Vector<EasyFilter<V, E>>	filters;

	public CompositeFilter() {
		this.filters = new Vector<EasyFilter<V, E>>();
	}

	@Override
	public boolean acceptEdge(E edge) {
		for (EasyFilter<V, E> f : this.filters)
			if (f.isActive() && !f.acceptEdge(edge))
				return false;
		return true;
	}

	@Override
	public boolean acceptVertex(V vertex) {
		for (EasyFilter<V, E> f : this.filters)
			if (f.isActive() && !f.acceptVertex(vertex))
				return false;
		return true;
	}

	public void add(EasyFilter<V, E> filter) {
		this.filters.add(filter);
		filter.setParent(this);
	}

	public void addFilter(EasyFilter<V, E> filter) {
		this.filters.add(filter);
		filter.setParent(this);
	}

	@Override
	public Enumeration<EasyFilter<V, E>> children() {
		return this.filters.elements();
	}

	/*
	 * Sets this filter and all descendents to inactive As opposed to
	 * setActive(false), which only applies to this filter.
	 */
	@Override
	public void deactivate() {
		for (EasyFilter<V, E> f : this.filters)
			f.deactivate();
		this.active = false;
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		return this.filters.elementAt(arg0);
	}

	@Override
	public int getChildCount() {
		return this.filters.size();
	}

	@Override
	public int getIndex(TreeNode arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	public boolean removeFilter(EasyFilter<V, E> filter) {
		if (this.filters.contains(filter)) {
			this.filters.remove(filter);
			return true;
		} else
			return false;
	}
}
