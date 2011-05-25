/**
 * This class creates a visualization of a Simternet run using the JUNG library.
 * 
 * Runs an instance of Simternet and creates a GUI display that shows the state of the network.
 * 
 * @author Grayson Wright
 */

package simternet;

import java.awt.Dimension;

import simternet.application.AppCategory;
import simternet.application.ApplicationProvider;
import simternet.jung.BackboneLocationTransformer;
import simternet.jung.BackbonePaintTransformer;
import simternet.jung.BackboneStrokeTransformer;
import simternet.jung.CompositeLocationTransformer;
import simternet.jung.DatacenterLocationTransformer;
import simternet.jung.EdgeLocationTransformer;
import simternet.jung.GUI;
import simternet.jung.RandomLocationTransformer;
import simternet.network.Backbone;
import simternet.network.BackboneLink;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.nsp.NetworkProvider;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

public class SimternetWithJung {

	private Graph<Network, BackboneLink>	graph;
	private GUI								gui;
	private long							seed;
	private Simternet						sim;
	// private AbstractLayout<Network, BackboneLink> layout;
	private int								stepCount;

	public static void main(String[] args) {
		SimternetWithJung simWithJung = new SimternetWithJung();
		// simWithJung.start();
	}

	SimternetWithJung() {
		this.init();
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

		this.gui.pack();
		this.gui.setVisible(true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private VisualizationViewer<Network, BackboneLink> initGraphViewer() {

		this.graph = new DirectedSparseGraph<Network, BackboneLink>();

		// Set up transformers to move the vertices to the correct pixel
		// coordinates, depending on the type of Network it is.
		EdgeLocationTransformer edgeLocTfmr = new EdgeLocationTransformer(new Dimension(50, 50));
		BackboneLocationTransformer bBoneLocTfmr = new BackboneLocationTransformer(new Dimension(100, 500));
		DatacenterLocationTransformer dcenterLocTfmr = new DatacenterLocationTransformer(new Dimension(100, 500));
		RandomLocationTransformer randomLocTfmr = new RandomLocationTransformer(new Dimension(400, 400));

		// Now, put all of these transformers into a composite data structure
		// that will handle any type of Network.
		// The dimension argument is the pixel offset from the top left of the
		// composite transformer.
		CompositeLocationTransformer compositeTransformer = new CompositeLocationTransformer();
		compositeTransformer.addTransformer(edgeLocTfmr, 1, new Dimension(100, 100));
		compositeTransformer.addTransformer(bBoneLocTfmr, 1, new Dimension(600, 100));
		compositeTransformer.addTransformer(dcenterLocTfmr, 1, new Dimension(800, 100));
		compositeTransformer.addTransformer(randomLocTfmr, 10, new Dimension(200, 200));

		AbstractLayout<Network, BackboneLink> layout = new StaticLayout<Network, BackboneLink>(this.graph,
				compositeTransformer);

		Dimension frameDimension = new Dimension(1000, 1000);

		VisualizationViewer<Network, BackboneLink> viewer = new VisualizationViewer<Network, BackboneLink>(layout,
				frameDimension);
		viewer.getModel().setGraphLayout(layout, frameDimension);

		// Set up transformers to label and color the vertices and edges.
		// viewer.getRenderContext().setVertexLabelTransformer(new
		// ToStringLabeller());
		// this.viewer.getRenderContext().setEdgeLabelTransformer(new
		// ToStringLabeller());
		viewer.getRenderContext().setEdgeStrokeTransformer(new BackboneStrokeTransformer());
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

		this.gui.setVisible(false);
		this.gui.dispose();

		this.stepCount = 0;
		this.sim = new Simternet(this.seed);

		// re-initialize GUI - work is done in simternet.jung.GUI
		this.gui = new GUI(this, this.initGraphViewer());
		this.gui.setSeedLabel(this.seed);
		this.gui.setStepLabel(0);

		this.gui.pack();
		this.gui.setVisible(true);
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

		this.gui.pack();
		this.gui.setVisible(true);
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
				this.graph.addEdge(backbone.getEgressLink(edge), backbone, edge);
			}
		}

		// this.viewer.repaint();
		this.gui.repaint();
	}
}