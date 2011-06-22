package simternet.jung.filter;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * A composite filter structure that enables many different filters to be
 * applied at the same time. In short, if an object passes all of the filters in
 * the composite structure, then it passes. If it is rejected by any of the
 * filters, then it is rejected.
 * 
 * @author graysonwright
 * 
 * @param <V>
 *            the vertex type
 * @param <E>
 *            the edge type
 */
public class CompositeFilter<V, E> extends EasyFilter<V, E> {

	private Vector<EasyFilter<V, E>>	filters;

	public CompositeFilter() {
		this.filters = new Vector<EasyFilter<V, E>>();
	}

	/**
	 * Accepts the edge if all of the sub-filters accept it. Rejects the edge if
	 * any of the sub-filters reject it.
	 */
	@Override
	public boolean acceptEdge(E edge) {
		for (EasyFilter<V, E> f : this.filters)
			if (f.isActive() && !f.acceptEdge(edge))
				return false;
		return true;
	}

	/**
	 * Accepts the vertex if all of the sub-filters accept it. Rejects the
	 * vertex if any of the sub-filters reject it.
	 */
	@Override
	public boolean acceptVertex(V vertex) {
		for (EasyFilter<V, E> f : this.filters)
			if (f.isActive() && !f.acceptVertex(vertex))
				return false;
		return true;
	}

	/**
	 * Add a filter to the composite data structure
	 * 
	 * @param filter
	 *            a filter to add
	 */
	public void add(EasyFilter<V, E> filter) {
		this.filters.add(filter);
		filter.setParent(this);
	}

	@Override
	public Enumeration<EasyFilter<V, E>> children() {
		return this.filters.elements();
	}

	/*
	 * Sets this filter and all descendents to inactive - As opposed to
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
		return 0;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	/**
	 * Removes a filter from the composite structure
	 * 
	 * @param filter
	 *            the filter to remove
	 * @return true if the filter was present and was removed, false if the
	 *         filter was never present
	 */
	public boolean removeFilter(EasyFilter<V, E> filter) {
		if (this.filters.contains(filter)) {
			this.filters.remove(filter);
			return true;
		} else
			return false;
	}
}
