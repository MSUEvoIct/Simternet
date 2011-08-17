package simternet.gui.filter;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * A JUNG layout that allows easy filtering. Necessary in order to use the
 * EasyFilter class.
 * 
 * @author graysonwright
 */
public class EasyFilterLayout<V, E> extends StaticLayout<V, E> {

	private EasyFilter<V, E>	filter;

	/**
	 * Initializes object with a graph and a transformer that defines the
	 * locations of graph objects
	 * 
	 * @param graph
	 *            the graph to be represented
	 * @param transformer
	 *            a transformer that defines the location of the graphed objects
	 */
	public EasyFilterLayout(DirectedSparseGraph<V, E> graph, Transformer<V, Point2D> transformer) {
		super(graph, transformer);
	}

	/**
	 * Initializes object with a graph and a filter that defines which objects
	 * to display in the graph
	 * 
	 * @param graph
	 *            the graph to be represented
	 * @param f
	 *            the filter to apply to the graph
	 */
	public EasyFilterLayout(Graph<V, E> graph, EasyFilter<V, E> f) {
		super(f.transform(graph));
		this.filter = f;
	}

	/**
	 * @return the filter currently being applied to the graph
	 */
	public EasyFilter<V, E> getFilter() {
		return this.filter;
	}

	/**
	 * Sets a new filter to apply to the graph
	 * 
	 * @param filter
	 *            the new filter to use
	 */
	public void setFilter(EasyFilter<V, E> filter) {
		this.filter = filter;
		super.setGraph(this.filter.transform(super.getGraph()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.jung.algorithms.layout.AbstractLayout#setGraph(edu.uci.ics
	 * .jung.graph.Graph)
	 * 
	 * The filtering takes place as the graph is added to the layout.
	 * Unfortunately, this means that whenever you want to update the graph, you
	 * have to call setGraph() again.
	 */
	@Override
	public void setGraph(Graph<V, E> g) {
		if (this.filter != null)
			super.setGraph(this.filter.transform(g));
	}
}
