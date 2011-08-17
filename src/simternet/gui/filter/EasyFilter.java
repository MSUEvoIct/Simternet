package simternet.gui.filter;

import java.util.Collection;

import javax.swing.tree.TreeNode;

import edu.uci.ics.jung.algorithms.filters.Filter;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * An easily-subclassable filter that only requires the implementation of two
 * functions.
 * 
 * @author graysonwright
 * 
 * @param <V>
 *            the graph vertex type
 * @param <E>
 *            the graph edge type
 */
public abstract class EasyFilter<V, E> implements Filter<V, E>, TreeNode {

	protected boolean			active;
	private EasyFilter<V, E>	parent;

	public EasyFilter() {
		this.active = false;
	}

	/**
	 * Decides whether or not to display an edge in a graph.
	 * 
	 * @param edge
	 *            the edge whose fate is to be decided
	 * @return true if the edge is to be included in the graph's display, false
	 *         if it is to be excluded
	 */
	public abstract boolean acceptEdge(E edge);

	/**
	 * Decides whether or not to display a vertex in a graph
	 * 
	 * @param vertex
	 *            the vertex whose fate is to be decided
	 * @return true if the vertex is to be included in the graph's display,
	 *         false if it is to be excluded
	 */
	public abstract boolean acceptVertex(V vertex);

	/**
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

	/**
	 * Used if part of a composite structure
	 */
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

	/**
	 * Used if part of a composite structure. Allows this object to retain a
	 * reference to its parent
	 * 
	 * @param p
	 *            the object's parent.
	 */
	public void setParent(EasyFilter<V, E> p) {
		this.parent = p;
	}

	/**
	 * Filters a graph based on the results of acceptEdge(E) and acceptVertex(V)
	 * 
	 * @param inGraph
	 *            the graph to be filtered
	 * @return the graph after applying the filter
	 */
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
