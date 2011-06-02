/**
 * This class creates a visualization of a Simternet run using the JUNG library.
 * 
 * Runs an instance of Simternet and creates a GUI display that shows the state of the network.
 * 
 * @author Grayson Wright
 */

package simternet;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;

import org.apache.commons.collections15.Transformer;

import sim.util.Int2D;
import simternet.application.AppCategory;
import simternet.application.ApplicationProvider;
import simternet.jung.GUI;
import simternet.jung.appearance.BackbonePaintTransformer;
import simternet.jung.appearance.BackboneStrokeTransformer;
import simternet.jung.appearance.EdgePaintTransformer;
import simternet.jung.appearance.EdgeShapeTransformer;
import simternet.jung.filter.CompositeFilter;
import simternet.jung.filter.DatacenterNameFilter;
import simternet.jung.filter.EasyFilter;
import simternet.jung.filter.EasyFilterLayout;
import simternet.jung.filter.FilterGUI;
import simternet.jung.filter.HighPassFilter;
import simternet.jung.filter.SingleEdgeFilter;
import simternet.jung.location.BackboneLocationTransformer;
import simternet.jung.location.CompositeLocationTransformer;
import simternet.jung.location.DatacenterLocationTransformer;
import simternet.jung.location.EdgeLocationTransformer;
import simternet.jung.location.RandomLocationTransformer;
import simternet.network.Backbone;
import simternet.network.BackboneLink;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.nsp.NetworkProvider;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

public class SimternetWithJung {

	private EasyFilter<Network, BackboneLink>			filter;
	private FilterGUI									filterGUI;
	private DirectedSparseGraph<Network, BackboneLink>	graph;
	private GUI											gui;
	private EasyFilterLayout<Network, BackboneLink>		layout;
	private long										seed;
	private Simternet									sim;
	private int											stepCount;

	public static void main(String[] args) {
		new SimternetWithJung().start();
	}

	SimternetWithJung() {
		this.init();
	}

	public void filterButtonPressed() {
		if (this.filterGUI == null) {
			this.filterGUI = new FilterGUI(this, this.filter);
			this.filterGUI.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}

		if (!this.filterGUI.isVisible())
			this.filterGUI.setVisible(true);
	}

	private void init() {
		this.stepCount = 0;

		// Initialize simulation
		this.seed = System.currentTimeMillis();
		this.sim = new Simternet(this.seed);

		// Initialize GUI - work is done in simternet.jung.GUI
		this.gui = new GUI(this, this.initGraphViewer());
		this.gui.setSeedLabel(this.seed);
		this.gui.setStepLabel(0);

		this.setUpFilters();

		this.gui.pack();
		this.gui.setVisible(true);
	}

	// @SuppressWarnings({ "rawtypes", "unchecked" })
	private VisualizationViewer<Network, BackboneLink> initGraphViewer() {

		this.graph = new DirectedSparseGraph<Network, BackboneLink>();
		this.layout = new EasyFilterLayout<Network, BackboneLink>(this.graph, this.setUpLocationTransformer());

		Dimension frameDimension = new Dimension(1000, 1000);

		VisualizationViewer<Network, BackboneLink> viewer = new VisualizationViewer<Network, BackboneLink>(this.layout,
				frameDimension);
		viewer.getModel().setGraphLayout(this.layout, frameDimension);

		// Set up transformers to label and color the vertices and edges.

		// Adjust Vertex Size:
		viewer.getRenderContext().setVertexShapeTransformer(new EdgeShapeTransformer(this.sim));

		// Adjust Vertex Color:
		viewer.getRenderContext().setVertexFillPaintTransformer(new EdgePaintTransformer(this.sim));

		// Label Vertices:
		// viewer.getRenderContext().setVertexLabelTransformer(new
		// ToStringLabeller<Network>());

		// Label Edges:
		// viewer.getRenderContext().setEdgeLabelTransformer(new
		// ToStringLabeller<BackboneLink>());

		// Set BackboneLink thickness:
		viewer.getRenderContext().setEdgeStrokeTransformer(new BackboneStrokeTransformer());
		// Set BackboneLink color:
		viewer.getRenderContext().setEdgeDrawPaintTransformer(new BackbonePaintTransformer());

		// Allow the mouse to pick and move vertices and edges.
		@SuppressWarnings("rawtypes")
		final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		viewer.setGraphMouse(graphMouse);
		viewer.addKeyListener(graphMouse.getModeKeyListener());
		viewer.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");

		return viewer;
	}

	public void resetNewSeed() {
		this.seed = System.currentTimeMillis();
		this.resetSameSeed();
	}

	public void resetSameSeed() {
		this.gui.setVisible(false);
		this.gui.dispose();

		this.stepCount = 0;
		this.sim = new Simternet(this.seed);

		// re-initialize GUI - work is done in simternet.jung.GUI
		this.gui = new GUI(this, this.initGraphViewer());
		this.gui.setSeedLabel(this.seed);
		this.gui.setStepLabel(0);

		this.setUpFilters();

		this.gui.pack();
		this.gui.setVisible(true);
		this.start();
	}

	private void setUpFilters() {

		// TODO: user-generated filters, not hard-coded ones.
		this.filter = new CompositeFilter<Network, BackboneLink>();
		((CompositeFilter<Network, BackboneLink>) this.filter).add(new SingleEdgeFilter(new Int2D(3, 1)));
		((CompositeFilter<Network, BackboneLink>) this.filter).add(new DatacenterNameFilter("Datacenter of ASP-2"));
		((CompositeFilter<Network, BackboneLink>) this.filter).add(new HighPassFilter(1000000.0));
	}

	private Transformer<Network, Point2D> setUpLocationTransformer() {
		// Set up transformers to move the vertices to the correct pixel
		// coordinates, depending on the type of Network it is.
		EdgeLocationTransformer edgeLocTfmr = new EdgeLocationTransformer(new Dimension(50, 50));
		BackboneLocationTransformer bBoneLocTfmr = new BackboneLocationTransformer(new Dimension(100, 500));
		DatacenterLocationTransformer dcenterLocTfmr = new DatacenterLocationTransformer(new Dimension(100, 500));
		RandomLocationTransformer<Network> randomLocTfmr = new RandomLocationTransformer<Network>(new Dimension(400,
				400));

		// Now, put all of these transformers into a composite data structure
		// that will handle any type of Network.
		// The dimension argument is the pixel offset from the top left of the
		// composite transformer.
		CompositeLocationTransformer<Network> compositeTransformer = new CompositeLocationTransformer<Network>();
		compositeTransformer.addTransformer(edgeLocTfmr, 1, new Dimension(100, 100));
		compositeTransformer.addTransformer(bBoneLocTfmr, 1, new Dimension(600, 100));
		compositeTransformer.addTransformer(dcenterLocTfmr, 1, new Dimension(800, 100));
		compositeTransformer.addTransformer(randomLocTfmr, 10, new Dimension(200, 200));

		return compositeTransformer;
	}

	public void start() {
		// start simulation
		this.sim.start();
		this.stepCount = 0;
		this.step(this.stepCount);
	}

	public void step(int n) {
		for (int i = 0; i < n; i++)
			this.sim.schedule.step(this.sim);

		// update stepCount Label
		this.stepCount += n;
		this.gui.setStepLabel(this.stepCount);

		this.updateGraph();
	}

	/*
	 * updateFilters
	 * 
	 * action method, called by FilterGUI.
	 * 
	 * input: paths - an array of TreePaths representing all checked Filters in
	 * the tree.
	 */
	public void updateFilters(TreePath[] paths) {
		// deactivate the root filter, which will deactivate all of its
		// sub-filters.
		this.filter.deactivate();

		// Then, re-activate all checked filters
		if (paths != null)
			for (TreePath tp : paths)
				if (tp.getLastPathComponent() instanceof EasyFilter<?, ?>) {

					EasyFilter<?, ?> filter = ((EasyFilter<?, ?>) tp.getLastPathComponent());
					filter.setActive(true);
				}

		this.updateGraph();
	}

	public void updateGraph() {

		for (ApplicationProvider asp : this.sim.getASPs(AppCategory.COMMUNICATION)) {
			this.graph.addVertex(asp.getDataCenter());
			for (Network net : asp.getConnectedNetworks()) {
				this.graph.addVertex(net);
				this.graph.addEdge(asp.getDataCenter().getEgressLink(net), asp.getDataCenter(), net);
			}
		}

		for (NetworkProvider nsp : this.sim.getNetworkServiceProviders()) {
			// TODO: Display Each NSP in a different color

			/*
			 * If a vertex or edge is already represented in the graph, it will
			 * not be duplicated by this function. It is safe to add an object
			 * multiple times.
			 */
			Backbone backbone = nsp.getBackboneNetwork();
			this.graph.addVertex(backbone);

			for (EdgeNetwork edge : nsp.getEdgeNetworks()) {
				this.graph.addVertex(edge);
				// System.err.println("Adding Vertex " + edge);
				this.graph.addEdge(backbone.getEgressLink(edge), backbone, edge);
			}
		}

		// this.graph = (DirectedSparseGraph<Network, BackboneLink>)
		// this.filter.transform(this.graph);
		this.layout.setFilter(this.filter);

		this.layout.setGraph(this.graph);

		// this.viewer.repaint();
		this.gui.repaint();
	}
}