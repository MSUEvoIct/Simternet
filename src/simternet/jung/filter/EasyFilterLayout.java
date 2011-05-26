package simternet.jung.filter;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class EasyFilterLayout<V, E> extends StaticLayout<V, E> {

	private EasyFilter<V, E>	filter;

	public EasyFilterLayout(DirectedSparseGraph<V, E> graph, Transformer<V, Point2D> transformer) {
		super(graph, transformer);
	}

	public EasyFilterLayout(Graph<V, E> graph, EasyFilter<V, E> f) {
		super(f.transform(graph));
		this.filter = f;
	}

	public EasyFilter<V, E> getFilter() {
		return this.filter;
	}

	public void setFilter(EasyFilter<V, E> f) {
		this.filter = f;
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
		if (this.filter != null) {
			super.setGraph(this.filter.transform(g));
			this.filter.print();
		}
	}
}
