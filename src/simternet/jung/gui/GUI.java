package simternet.jung.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.tree.TreePath;

import org.apache.commons.collections15.Transformer;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.jung.appearance.BackbonePaintTransformer;
import simternet.jung.appearance.BackboneStrokeTransformer;
import simternet.jung.appearance.EdgePaintTransformer;
import simternet.jung.appearance.NetworkLabeller;
import simternet.jung.appearance.NetworkShapeTransformer;
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
import simternet.network.Datacenter;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.nsp.NetworkProvider;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

public class GUI extends JPanel {

	protected ControlPanel									controlPanel;
	private EasyFilter<Network, BackboneLink>				filter;
	private FilterGUI										filterGUI;
	private DirectedSparseGraph<Network, BackboneLink>		graph;
	protected InfoPanel										infoPanel;
	protected HashMap<Object, Inspector>					inspectors;
	private EasyFilterLayout<Network, BackboneLink>			layout;
	protected Simternet										simternet;
	protected VisualizationViewer<Network, BackboneLink>	viewer;

	private static final long								serialVersionUID	= 1L;

	public static void main(String[] args) {
		JFrame jFrame = new JFrame("Simternet Checkpoint Reader");
		jFrame.setContentPane(new GUI(new Simternet(System.currentTimeMillis())));
		((GUI) jFrame.getContentPane()).start();
		jFrame.pack();
		jFrame.setVisible(true);
	}

	/**
	 * Initializes the GUI with a given Simternet simulation
	 * 
	 * @param simternet
	 *            the simulation to display
	 */
	public GUI(Simternet simternet) {
		super();
		this.simternet = simternet;
		this.initComponents();
	}

	/**
	 * Called by ControlPanel when the filter button is pressed. Opens a JFrame
	 * that allows the user to pick filters to apply
	 */
	public void filterButtonPressed() {
		if (this.filterGUI == null) {
			this.filterGUI = new FilterGUI(this, this.filter);
			this.filterGUI.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}

		if (!this.filterGUI.isVisible())
			this.filterGUI.setVisible(true);
	}

	/**
	 * Defines the layout of the GUI
	 */
	protected void initComponents() {
		this.inspectors = new HashMap<Object, Inspector>();

		this.setLayout(new BorderLayout());

		this.controlPanel = new ControlPanel(this);
		this.controlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.add(this.controlPanel, BorderLayout.EAST);

		this.infoPanel = new InfoPanel(this.simternet);
		this.infoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.add(this.infoPanel, BorderLayout.SOUTH);

		this.initViewer();
		this.add(this.viewer, BorderLayout.CENTER);

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	/**
	 * Initializes the VisualizationViewer used for displaying the Simternet
	 * graph. Initializes transformers to modify the portrayal of the graph
	 */
	protected void initViewer() {

		this.graph = new DirectedSparseGraph<Network, BackboneLink>();
		this.layout = new EasyFilterLayout<Network, BackboneLink>(this.graph, this.setUpLocationTransformer());

		Dimension frameDimension = new Dimension(1000, 1000);
		this.viewer = new VisualizationViewer<Network, BackboneLink>(this.layout, frameDimension);
		this.viewer.getModel().setGraphLayout(this.layout, frameDimension);

		// Set up transformers to label and color the vertices and edges.

		// Adjust vertex shape/size:
		this.viewer.getRenderContext().setVertexShapeTransformer(new NetworkShapeTransformer(this.simternet));

		// Adjust vertex color:
		this.viewer.getRenderContext().setVertexFillPaintTransformer(new EdgePaintTransformer(this.simternet));

		// Label vertices:
		this.viewer.getRenderContext().setVertexLabelTransformer(new NetworkLabeller());

		// Label edges:
		// viewer.getRenderContext().setEdgeLabelTransformer(new
		// ToStringLabeller<BackboneLink>());

		// Set BackboneLink thickness:
		this.viewer.getRenderContext().setEdgeStrokeTransformer(new BackboneStrokeTransformer());
		// Set BackboneLink color:
		this.viewer.getRenderContext().setEdgeDrawPaintTransformer(new BackbonePaintTransformer());

		// Allow the mouse to pick and move vertices and edges.
		AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse<Network, BackboneLink>();
		// Use a special PickPlugin to notify this GUI class when a vertex is
		// picked.
		graphMouse.add(new VertexPickPlugin(this));
		this.viewer.setGraphMouse(graphMouse);
		this.viewer.addKeyListener(graphMouse.getModeKeyListener());
		this.viewer.setToolTipText("<html><center>Drag and scroll to translate and zoom<p>Control-click to inspect");

		this.viewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

	}

	public void removeInspector(Object object) {
		// FIXME: This is not correctly removing the inspectors. Slight memory
		// leak.
		if (this.inspectors.containsKey(object))
			this.inspectors.remove(object);
	}

	/**
	 * Sets a new Simternet object for the GUI to display. Called when the user
	 * loads a new Simternet object from a checkpoint file, or when they reload
	 * the current checkpoint from the beginning.
	 * 
	 * @param sim
	 *            - The new Simternet object to be represented
	 */
	public void setSimternet(Simternet sim) {
		// clean up from last checkpoint
		for (Inspector i : this.inspectors.values())
			i.dispose();
		this.inspectors.clear();

		this.simternet = sim;
		this.remove(this.viewer);
		this.initViewer();
		this.add(this.viewer, BorderLayout.CENTER);
		this.updateAll();

		// refresh the labels in our InfoPanel
		this.infoPanel.setGeneration(sim.generation);
		this.infoPanel.setChunk(sim.chunk);
		this.infoPanel.setStep(sim.schedule.getSteps());
	}

	/**
	 * Initializes filters (initially non-active) that the user can later apply
	 * to the simulation
	 */
	protected void setUpFilters() {
		// TODO: user-generated filters, not hard-coded ones.
		this.filter = new CompositeFilter<Network, BackboneLink>();
		((CompositeFilter<Network, BackboneLink>) this.filter).add(new SingleEdgeFilter(new Int2D(3, 1)));
		((CompositeFilter<Network, BackboneLink>) this.filter).add(new DatacenterNameFilter("Datacenter of ASP-2"));
		((CompositeFilter<Network, BackboneLink>) this.filter).add(new HighPassFilter(1000000.0));
	}

	/**
	 * Initializes and returns a Transformer that will lay out the contents of
	 * the graph onscreen
	 * 
	 * @return a transformer that dictates where in the visualization of the
	 *         graph each item should be displayed
	 */
	protected Transformer<Network, Point2D> setUpLocationTransformer() {
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

	/**
	 * Starts a simternet simulation and updates the GUI components to reflect
	 * the changes
	 */
	protected void start() {
		// start simulation
		this.simternet.start();
		this.updateAll();
	}

	/**
	 * Steps the Simternet simulation a given number of times, and updates the
	 * GUI components to reflect any changes
	 * 
	 * @param n
	 *            the number of steps to execute
	 */
	public void step(int n) {
		for (int i = 0; i < n; i++)
			this.simternet.schedule.step(this.simternet);

		this.updateAll();
	}

	/**
	 * Updates the GUI's components (the graph, infoPanel, and inspectors)
	 */
	protected void updateAll() {
		// Update the graph visualization
		this.updateGraph();

		// update the stepCount Label in the infoPanel
		this.infoPanel.setStep(this.simternet.schedule.getSteps());

		// Update each of the inspectors
		for (Inspector i : this.inspectors.values())
			i.update();
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

		this.updateAll();
	}

	public void updateGraph() {

		if (this.filter == null)
			this.setUpFilters();

		for (ApplicationProvider asp : this.simternet.getASPs()) {
			this.graph.addVertex(asp.getDataCenter());
			for (Network net : asp.getConnectedNetworks()) {
				this.graph.addVertex(net);
				this.graph.addEdge(asp.getDataCenter().getEgressLink(net), asp.getDataCenter(), net);
			}
		}

		for (NetworkProvider nsp : this.simternet.getNetworkServiceProviders()) {
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
		this.repaint();
	}

	/**
	 * Opens an inspector window for the given vertex
	 * 
	 * @param vertex
	 *            the vertex to inspect
	 */
	public void vertexPicked(Network vertex) {

		if (this.inspectors.containsKey(vertex)) {
			Inspector i = this.inspectors.get(vertex);
			i.toFront();
		} else {
			Inspector inspector = null;

			if (vertex instanceof EdgeNetwork)
				inspector = new LocationInspector(((EdgeNetwork) vertex).getLocation(), this);
			else if (vertex instanceof Backbone)
				inspector = new NetworkProviderInspector(((Backbone) vertex).getOwner(), this);
			else if (vertex instanceof Datacenter)
				inspector = new ApplicationProviderInspector(((Datacenter) vertex).getOwner(), this);

			if (inspector != null) {
				this.inspectors.put(vertex, inspector);
				inspector.pack();
				inspector.setVisible(true);
			}
		}
	}
}
