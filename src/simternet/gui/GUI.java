package simternet.gui;

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
import simternet.gui.filter.CompositeFilter;
import simternet.gui.filter.DatacenterNameFilter;
import simternet.gui.filter.EasyFilter;
import simternet.gui.filter.EasyFilterLayout;
import simternet.gui.filter.HighPassFilter;
import simternet.gui.filter.SingleEdgeFilter;
import simternet.gui.inspector.ApplicationProviderInspector;
import simternet.gui.inspector.ConsumerNetworkInspector;
import simternet.gui.inspector.GlobalASPInspector;
import simternet.gui.inspector.GlobalEdgeInspector;
import simternet.gui.inspector.GlobalNSPInspector;
import simternet.gui.inspector.Inspector;
import simternet.gui.inspector.NetworkProviderInspector;
import simternet.jung.ConsumerNetwork;
import simternet.jung.LocationTransformer;
import simternet.jung.VertexPickPlugin;
import simternet.jung.appearance.LinkPaintTransformer;
import simternet.jung.appearance.LinkStrokeTransformer;
import simternet.jung.appearance.NetworkLabeller;
import simternet.jung.appearance.NetworkPaintTransformer;
import simternet.jung.appearance.NetworkShapeTransformer;
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

/**
 * A GUI that displays a serialized Simternet object and allows the user to step
 * through the simulation and see how the network changes. Relies on the
 * simternet.jung packages to display the representation of the network, and the
 * simternet.gui packages to display additional information.
 * 
 * @author graysonwright
 * 
 */
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

	protected Simternet										simternet;

	/**
	 * Creates a JFrame to contain a GUI object.
	 * 
	 * @param args
	 *            runtime arguments
	 */
	public static void main(String[] args) {
		JFrame jFrame = new JFrame("Simternet Checkpoint Reader");
		GUI gui = new GUI(null);

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
		this.simternet = simternet;
		initComponents();
	}

	/**
	 * Used by related classes (most notably, ControlPanel) so they can change
	 * their behavior if there is not a Simternet instance loaded.
	 * 
	 * @return true if there is a simternet instance, false otherwise
	 */
	public boolean hasValidSimternetInstance() {
		return simternet != null;
	}

	/**
	 * 
	 * Called by ControlPanel when the "Inspect ASPs" button is pressed. Creates
	 * an instance of GlobalASPInspector and displays it onscreen.
	 */
	public void aspInspectorButtonPressed() {
		if (hasValidSimternetInstance()) {
			GlobalASPInspector inspector = new GlobalASPInspector(simternet);
			inspectors.add(inspector);
			inspector.pack();
			inspector.setVisible(true);
		} else {
			System.err.println("Cannot load inspector because no serialized instance is currently loaded.");
		}
	}

	/**
	 * Called by ControlPanel when the "Inspect Edges" button is pressed.
	 * Creates an instance of GlobalEdgeInspector and displays it onscreen.
	 */
	public void edgeInspectorButtonPressed() {
		if (hasValidSimternetInstance()) {
			GlobalEdgeInspector inspector = new GlobalEdgeInspector(simternet);
			inspectors.add(inspector);
			inspector.pack();
			inspector.setVisible(true);
		} else {
			System.err.println("Cannot load inspector because no serialized instance is currently loaded.");
		}
	}

	/**
	 * Called by ControlPanel when the filter button is pressed. Creates a
	 * FilterGUI instance that allows the user to apply filters
	 */
	public void filterButtonPressed() {
		if (filterGUI == null) {
			filterGUI = new FilterGUI(this, filter);
			filterGUI.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}

		if (!filterGUI.isVisible()) {
			filterGUI.setVisible(true);
		}
	}

	/**
	 * Initializes some variables and defines the layout of the GUI
	 */
	protected void initComponents() {

		inspectors = new HashSet<Inspector>();

		setLayout(new BorderLayout());

		// Put a control panel on the right side
		controlPanel = new ControlPanel(this);
		controlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.add(controlPanel, BorderLayout.EAST);

		// Information goes on bottom
		infoPanel = new InfoPanel(simternet);
		infoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.add(infoPanel, BorderLayout.SOUTH);

		// And the graph gets center stage
		initViewer();
		this.add(viewer, BorderLayout.CENTER);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	/**
	 * Initializes the VisualizationViewer used for displaying the Simternet
	 * graph. Initializes transformers to modify the appearance of the graph
	 */
	protected void initViewer() {

		graph = new DirectedSparseGraph<Network, BackboneLink>();

		// Plug in our own special layout that enables filtering, and use our
		// LocationTransformer class to lay everything out.
		layout = new EasyFilterLayout<Network, BackboneLink>(graph, new LocationTransformer(simternet));

		Dimension frameDimension = new Dimension(800, 500);
		viewer = new VisualizationViewer<Network, BackboneLink>(layout, frameDimension);
		viewer.getModel().setGraphLayout(layout, frameDimension);

		// Set up transformers to label and color the vertices and edges.

		// Adjust vertex shape/size:
		viewer.getRenderContext().setVertexShapeTransformer(new NetworkShapeTransformer());

		// Adjust vertex color:
		viewer.getRenderContext().setVertexFillPaintTransformer(new NetworkPaintTransformer());

		// Label vertices:
		viewer.getRenderContext().setVertexLabelTransformer(new NetworkLabeller());

		// Label edges: (commented because they cluttered the display)
		// viewer.getRenderContext().setEdgeLabelTransformer(new
		// ToStringLabeller<BackboneLink>());

		// Set BackboneLink thickness:
		viewer.getRenderContext().setEdgeStrokeTransformer(new LinkStrokeTransformer());
		// Set BackboneLink color:
		viewer.getRenderContext().setEdgeDrawPaintTransformer(new LinkPaintTransformer());

		// Allow the mouse to pick and move vertices and edges.
		AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse<Network, BackboneLink>();
		// Use a special PickPlugin to notify this GUI class when a vertex is
		// picked.
		graphMouse.add(new VertexPickPlugin(this));
		viewer.setGraphMouse(graphMouse);
		viewer.addKeyListener(graphMouse.getModeKeyListener());
		viewer.setToolTipText("<html><center>Drag and scroll to translate and zoom<p>Control-click to inspect");

		viewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

	}

	/**
	 * Creates an instance of GlobalNSPInspector and displays it onscreen.
	 */
	public void nspInspectorButtonPressed() {
		if (hasValidSimternetInstance()) {
			GlobalNSPInspector inspector = new GlobalNSPInspector(simternet);
			inspectors.add(inspector);
			inspector.pack();
			inspector.setVisible(true);
		} else {
			System.err.println("Cannot load inspector because no serialized instance is currently loaded.");
		}
	}

	/**
	 * Called by ControlPanel when the user clicks the "View Data" button. Tells
	 * each inspector to print their data.
	 */
	public void printDataButtonPressed() {
		for (Inspector i : inspectors) {
			i.printData();
		}
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
		for (Inspector i : inspectors) {
			i.dispose();
		}
		inspectors.clear();
		this.remove(viewer);

		// load up the new one
		simternet = sim;
		initViewer();
		this.add(viewer, BorderLayout.CENTER);

		infoPanel.setSimternet(sim);
		updateAll();

	}

	/**
	 * Initializes filters (initially non-active) that the user can later apply
	 * to the simulation
	 */
	protected void setUpFilters() {
		// TODO: allow the user to generate filters, in place of these
		// hard-coded ones.
		filter = new CompositeFilter<Network, BackboneLink>();
		((CompositeFilter<Network, BackboneLink>) filter).add(new SingleEdgeFilter(new Int2D(3, 1)));
		((CompositeFilter<Network, BackboneLink>) filter).add(new DatacenterNameFilter("Datacenter of ASP-2"));
		((CompositeFilter<Network, BackboneLink>) filter).add(new HighPassFilter(1000000.0));
	}

	/**
	 * Starts a simternet simulation and updates the GUI components to reflect
	 * the changes
	 */
	protected void start() {
		// start simulation
		if (hasValidSimternetInstance()) {
			simternet.start();
			updateAll();
		}
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
			simternet.schedule.step(simternet);
		}

		updateAll();
	}

	/**
	 * Updates the GUI's components (the graph, infoPanel, and inspectors)
	 */
	protected void updateAll() {
		// Update the graph visualization
		updateGraph();

		// refresh the labels in our InfoPanel
		infoPanel.update();

		// Update each of the inspectors
		for (Inspector i : inspectors) {
			i.update();
		}
	}

	/**
	 * updateFilters
	 * 
	 * action method, called by FilterGUI after the user has (de)activated some
	 * filters
	 * 
	 * @param paths
	 *            an array of TreePaths representing all checked Filters in the
	 *            tree.
	 */
	public void updateFilters(TreePath[] paths) {
		// deactivate the root filter, which will deactivate all of its
		// sub-filters.
		filter.deactivate();

		// Then, re-activate all checked filters
		if (paths != null) {
			for (TreePath tp : paths)
				if (tp.getLastPathComponent() instanceof EasyFilter<?, ?>) {

					EasyFilter<?, ?> filter = (EasyFilter<?, ?>) tp.getLastPathComponent();
					filter.setActive(true);
				}
		}

		updateAll();
	}

	/**
	 * Updates the JUNG visualization of the Simternet network.
	 */
	public void updateGraph() {

		if (filter == null) {
			setUpFilters();
		}

		/*
		 * If a vertex or edge is already represented in the graph, it will not
		 * be duplicated by adding it again. It is safe to add an object
		 * multiple times. So that's what we'll do.
		 */

		// add ASPs
		for (ApplicationProvider asp : simternet.getASPs()) {
			graph.addVertex(asp.getDatacenter());
			for (Network net : asp.getConnectedNetworks()) {
				graph.addVertex(net);
				graph.addEdge(asp.getDatacenter().getEgressLink(net), asp.getDatacenter(), net);
			}
		}

		// add NSPs
		for (NetworkProvider nsp : simternet.getNetworkServiceProviders()) {
			Backbone backbone = nsp.getBackboneNetwork();
			graph.addVertex(backbone);

			// for each NSP, add all of its edge networks
			for (EdgeNetwork edge : nsp.getEdgeNetworks()) {
				graph.addVertex(ConsumerNetwork.get(edge));
				graph.addEdge(backbone.getEgressLink(edge), backbone, ConsumerNetwork.get(edge));
			}
		}

		layout.setFilter(filter);
		layout.setGraph(graph);

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

		if (vertex instanceof ConsumerNetwork) {
			inspector = new ConsumerNetworkInspector((ConsumerNetwork) vertex);
		} else if (vertex instanceof Backbone) {
			inspector = new NetworkProviderInspector(((Backbone) vertex).getOwner());
		} else if (vertex instanceof Datacenter) {
			inspector = new ApplicationProviderInspector(((Datacenter) vertex).getOwner());
		}

		if (inspector != null) {
			inspectors.add(inspector);
			inspector.pack();
			inspector.setVisible(true);
		}
	}
}