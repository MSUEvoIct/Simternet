/**
 * This class creates a visualization of a Simternet run using the JUNG library.
 * 
 * Runs an instance of Simternet and creates a GUI display that shows 
 * 
 * 
 * @author Grayson Wright
 */

package simternet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import simternet.application.AppCategory;
import simternet.application.ApplicationProvider;
import simternet.jung.BackboneLocationTransformer;
import simternet.jung.BackbonePaintTransformer;
import simternet.jung.BackboneStrokeTransformer;
import simternet.jung.CompositeLocationTransformer;
import simternet.jung.DatacenterLocationTransformer;
import simternet.jung.EdgeLocationTransformer;
import simternet.jung.RandomLocationTransformer;
import simternet.network.Backbone;
import simternet.network.BackboneLink;
import simternet.network.Datacenter;
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
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class SimternetWithJung extends JFrame {

	private static final long	serialVersionUID	= 1L;

	public static void main(String[] args) {
		SimternetWithJung simWithJung = new SimternetWithJung();
		simWithJung.start();
	}

	private JFrame										frame;
	private Graph<Network, BackboneLink>				graph;
	private AbstractLayout<Network, BackboneLink>		layout;
	private Simternet									sim;

	private VisualizationViewer<Network, BackboneLink>	viewer;

	SimternetWithJung() {
		this.init();
	}

	private void init() {

		// Initialize Simulation
		this.sim = new Simternet(System.currentTimeMillis());

		// Initialize GUI
		this.frame = new JFrame("Simternet -- Simulates the growth of the internet");

		this.initGraphDisplay();

		this.frame.add(this.viewer, BorderLayout.CENTER);

		JButton updateButton = new JButton("Update graph");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SimternetWithJung.this.update();
			}
		});
		this.frame.add(updateButton, BorderLayout.SOUTH);

		// Allow the mouse to pick and move vertices and edges.
		@SuppressWarnings("rawtypes")
		final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		this.viewer.setGraphMouse(graphMouse);
		this.viewer.addKeyListener(graphMouse.getModeKeyListener());
		this.viewer.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");

		this.frame.pack();
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setVisible(true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initGraphDisplay() {
		// TODO Auto-generated method stub

		this.graph = new DirectedSparseGraph<Network, BackboneLink>();

		// Set up transformers to move the vertices to the correct pixel
		// coordinates
		CompositeLocationTransformer compTransformer = new CompositeLocationTransformer();

		EdgeLocationTransformer edgeLocTfmr = new EdgeLocationTransformer(new Dimension(200, 200));
		BackboneLocationTransformer bBoneLocTfmr = new BackboneLocationTransformer(new Dimension(100, 500));
		DatacenterLocationTransformer dcenterLocTfmr = new DatacenterLocationTransformer(new Dimension(800, 500));
		RandomLocationTransformer randomLocTfmr = new RandomLocationTransformer(new Dimension(400, 400));

		compTransformer.addTransformer(edgeLocTfmr, 1, new Dimension(100, 100));
		compTransformer.addTransformer(bBoneLocTfmr, 1, new Dimension(600, 100));
		compTransformer.addTransformer(dcenterLocTfmr, 1, new Dimension(500, 100));
		compTransformer.addTransformer(randomLocTfmr, 10, new Dimension(200, 200));

		this.layout = new StaticLayout<Network, BackboneLink>(this.graph, compTransformer);

		Dimension frameDimension = new Dimension(1000, 1000);
		this.viewer = new VisualizationViewer<Network, BackboneLink>(this.layout, frameDimension);
		this.viewer.getModel().setGraphLayout(this.layout, frameDimension);

		// Set up transformers to label and color the vertices and edges.
		this.viewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		// this.viewer.getRenderContext().setEdgeLabelTransformer(new
		// ToStringLabeller());
		this.viewer.getRenderContext().setEdgeStrokeTransformer(new BackboneStrokeTransformer());
		this.viewer.getRenderContext().setEdgeDrawPaintTransformer(new BackbonePaintTransformer());
	}

	private void start() {
		// start simulation
		this.sim.start();
		for (int i = 0; i < 50; i++)
			this.sim.schedule.step(this.sim);
		System.out.println("Simulation Done!");
		this.update();
	}

	public void update() {

		this.sim.schedule.step(this.sim);

		for (ApplicationProvider asp : this.sim.getASPs(AppCategory.COMMUNICATION)) {
			this.graph.addVertex(asp.getDatacenter());
			Datacenter dc = asp.getDatacenter();

			for (Network net : dc.getPeers()) {
				this.graph.addVertex(net);
				this.graph.addEdge(asp.getDatacenter().getEgressLink(net), asp.getDatacenter(), net);
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

		this.viewer.repaint();
		this.frame.repaint();
	}
}