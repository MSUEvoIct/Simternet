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
			if (!f.acceptEdge(edge))
				return false;
		return true;
	}

	@Override
	public boolean acceptVertex(V vertex) {
		for (EasyFilter<V, E> f : this.filters)
			if (!f.acceptVertex(vertex))
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
	public Enumeration children() {
		return this.filters.elements();
	}

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

	@Override
	public void print() {
		super.print();
		for (EasyFilter<V, E> f : this.filters)
			f.print();
	}

	public boolean removeFilter(EasyFilter<V, E> filter) {
		if (this.filters.contains(filter)) {
			this.filters.remove(filter);
			return true;
		} else
			return false;
	}
}
