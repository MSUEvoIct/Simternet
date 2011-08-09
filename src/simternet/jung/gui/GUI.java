package simternet.jung.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.tree.TreePath;

import sim.util.Int2D;
import simternet.Simternet;
import simternet.application.ApplicationProvider;
import simternet.jung.ConsumerNetwork;
import simternet.jung.LocationTransformer;
import simternet.jung.VertexPickPlugin;
import simternet.jung.appearance.LinkPaintTransformer;
import simternet.jung.appearance.LinkStrokeTransformer;
import simternet.jung.appearance.NetworkLabeller;
import simternet.jung.appearance.NetworkPaintTransformer;
import simternet.jung.appearance.NetworkShapeTransformer;
import simternet.jung.filter.CompositeFilter;
import simternet.jung.filter.DatacenterNameFilter;
import simternet.jung.filter.EasyFilter;
import simternet.jung.filter.EasyFilterLayout;
import simternet.jung.filter.HighPassFilter;
import simternet.jung.filter.SingleEdgeFilter;
import simternet.jung.inspector.ApplicationProviderInspector;
import simternet.jung.inspector.ConsumerNetworkInspector;
import simternet.jung.inspector.GlobalASPInspector;
import simternet.jung.inspector.GlobalEdgeInspector;
import simternet.jung.inspector.GlobalNSPInspector;
import simternet.jung.inspector.Inspector;
import simternet.jung.inspector.NetworkProviderInspector;
import simternet.jung.inspector.property.TrackableProperty;
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
	protected HashSet<Inspector>							inspectors;
	private EasyFilterLayout<Network, BackboneLink>			layout;
	protected VisualizationViewer<Network, BackboneLink>	viewer;
	private static final long								serialVersionUID	= 1L;

	protected static Simternet								simternet;

	public static Simternet getSimternet() {
		return GUI.simternet;
	}

	public static void main(String[] args) {
		JFrame jFrame = new JFrame("Simternet Checkpoint Reader");
		GUI gui = new GUI(new Simternet(System.currentTimeMillis()));

		jFrame.setContentPane(gui);
		gui.start();
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
		GUI.simternet = simternet;
		this.initComponents();
	}

	public void ASPInspectorButtonPressed() {
		GlobalASPInspector inspector = new GlobalASPInspector(this);
		this.inspectors.add(inspector);
		inspector.pack();
		inspector.setVisible(true);
	}

	public void EdgeInspectorButtonPressed() {
		GlobalEdgeInspector inspector = new GlobalEdgeInspector(this);
		this.inspectors.add(inspector);
		inspector.pack();
		inspector.setVisible(true);
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

		TrackableProperty.setSimState(GUI.simternet);
		this.inspectors = new HashSet<Inspector>();

		this.setLayout(new BorderLayout());

		this.controlPanel = new ControlPanel(this);
		this.controlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.add(this.controlPanel, BorderLayout.EAST);

		this.infoPanel = new InfoPanel(GUI.simternet);
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

		// Define the layout, which will enable filtering, and use our
		// LocationTransformer class to lay everything out.
		this.layout = new EasyFilterLayout<Network, BackboneLink>(this.graph, new LocationTransformer(GUI.simternet));

		Dimension frameDimension = new Dimension(800, 500);
		this.viewer = new VisualizationViewer<Network, BackboneLink>(this.layout, frameDimension);
		this.viewer.getModel().setGraphLayout(this.layout, frameDimension);

		// Set up transformers to label and color the vertices and edges.

		// Adjust vertex shape/size:
		this.viewer.getRenderContext().setVertexShapeTransformer(new NetworkShapeTransformer(GUI.simternet));

		// Adjust vertex color:
		this.viewer.getRenderContext().setVertexFillPaintTransformer(new NetworkPaintTransformer(GUI.simternet));

		// Label vertices:
		this.viewer.getRenderContext().setVertexLabelTransformer(new NetworkLabeller());

		// Label edges:
		// viewer.getRenderContext().setEdgeLabelTransformer(new
		// ToStringLabeller<BackboneLink>());

		// Set BackboneLink thickness:
		this.viewer.getRenderContext().setEdgeStrokeTransformer(new LinkStrokeTransformer());
		// Set BackboneLink color:
		this.viewer.getRenderContext().setEdgeDrawPaintTransformer(new LinkPaintTransformer());

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

	public void NSPInspectorButtonPressed() {
		GlobalNSPInspector inspector = new GlobalNSPInspector(this);
		this.inspectors.add(inspector);
		inspector.pack();
		inspector.setVisible(true);
	}

	public void printDataButtonPressed() {
		for (Inspector i : this.inspectors)
			i.printData();
	}

	public void removeInspector(Inspector closedInspector) {
		this.inspectors.remove(closedInspector);
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
		for (Inspector i : this.inspectors)
			i.dispose();
		this.inspectors.clear();

		GUI.simternet = sim;
		this.remove(this.viewer);
		this.initViewer();
		this.add(this.viewer, BorderLayout.CENTER);

		this.infoPanel.setSimternet(sim);
		this.updateAll();

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
	 * Starts a simternet simulation and updates the GUI components to reflect
	 * the changes
	 */
	protected void start() {
		// start simulation
		GUI.simternet.start();
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
		for (int i = 0; i < n; i++) {
			GUI.simternet.schedule.step(GUI.simternet);
			for (Inspector inspector : this.inspectors)
				inspector.update();
		}

		this.updateAll();
	}

	/**
	 * Updates the GUI's components (the graph, infoPanel, and inspectors)
	 */
	protected void updateAll() {
		// Update the graph visualization
		this.updateGraph();

		// refresh the labels in our InfoPanel
		this.infoPanel.update();

		// Update each of the inspectors
		for (Inspector i : this.inspectors)
			i.update();
	}

	/**
	 * updateFilters
	 * 
	 * action method, called by FilterGUI.
	 * 
	 * @param paths
	 *            an array of TreePaths representing all checked Filters in the
	 *            tree.
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

		for (ApplicationProvider asp : GUI.simternet.getASPs()) {
			this.graph.addVertex(asp.getDataCenter());
			for (Network net : asp.getConnectedNetworks()) {
				this.graph.addVertex(net);
				this.graph.addEdge(asp.getDataCenter().getEgressLink(net), asp.getDataCenter(), net);
			}
		}

		for (NetworkProvider nsp : GUI.simternet.getNetworkServiceProviders()) {
			/*
			 * If a vertex or edge is already represented in the graph, it will
			 * not be duplicated by this function. It is safe to add an object
			 * multiple times.
			 */
			Backbone backbone = nsp.getBackboneNetwork();
			this.graph.addVertex(backbone);

			for (EdgeNetwork edge : nsp.getEdgeNetworks()) {
				this.graph.addVertex(ConsumerNetwork.get(edge));
				// System.err.println("Adding Vertex " + edge);
				this.graph.addEdge(backbone.getEgressLink(edge), backbone, ConsumerNetwork.get(edge));
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

		Inspector inspector = null;

		if (vertex instanceof ConsumerNetwork)
			inspector = new ConsumerNetworkInspector((ConsumerNetwork) vertex, this);
		else if (vertex instanceof Backbone)
			inspector = new NetworkProviderInspector(((Backbone) vertex).getOwner(), this);
		else if (vertex instanceof Datacenter)
			inspector = new ApplicationProviderInspector(((Datacenter) vertex).getOwner(), this);

		if (inspector != null) {
			this.inspectors.add(inspector);
			inspector.pack();
			inspector.setVisible(true);
		}
	}
}