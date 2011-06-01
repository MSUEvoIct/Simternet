package simternet.jung.filter;

import java.util.Collection;

import javax.swing.tree.TreeNode;

import edu.uci.ics.jung.algorithms.filters.Filter;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public abstract class EasyFilter<V, E> implements Filter<V, E>, TreeNode {

	protected boolean			active;
	private EasyFilter<V, E>	parent;

	public EasyFilter() {
		this.active = false;
	}

	public abstract boolean acceptEdge(E edge);

	public abstract boolean acceptVertex(V vertex);

	/*
	 * Sets this filter and all ancestors to active As opposed to
	 * setActive(true), which only applies to this filter.
	 */
	public void activate() {
		if (this.parent != null)
			this.parent.activate();
		this.active = true;
	}

	public void deactivate() {
		this.active = false;
	}

	@Override
	public TreeNode getParent() {
		return this.parent;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean flag) {
		this.active = flag;
	}

	public void setParent(EasyFilter<V, E> p) {
		this.parent = p;
	}

	public Graph<V, E> transform(Graph<V, E> inGraph) {
		if (!this.active)
			return inGraph;

		Graph<V, E> outGraph = new DirectedSparseGraph<V, E>();

		Collection<V> vertices = inGraph.getVertices();
		Collection<E> edges = inGraph.getEdges();

		for (V n : vertices)
			if (this.acceptVertex(n))
				outGraph.addVertex(n);

		for (E edge : edges)
			if (this.acceptEdge(edge))
				if (outGraph.containsVertex(inGraph.getSource(edge)) && outGraph.containsVertex(inGraph.getDest(edge)))
					outGraph.addEdge(edge, inGraph.getSource(edge), inGraph.getDest(edge));

		return outGraph;
	}
}
