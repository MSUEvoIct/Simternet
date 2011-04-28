/**
 * This class creates a visualization of a Simternet run using the JUNG library.
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
import simternet.application.ApplicationServiceProvider;
import simternet.jung.CompositeLayoutTransformer;
import simternet.jung.EdgeLayoutTransformer;
import simternet.jung.RandomLayoutTransformer;
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

public class SimternetWithJung extends JFrame {

	private Dimension									dimension;
	private JFrame										frame;
	private Graph<Network, BackboneLink>				graph;
	private AbstractLayout<Network, BackboneLink>		layout;
	private Simternet									sim;
	private VisualizationViewer<Network, BackboneLink>	viewer;

	public static void main(String[] args) {
		SimternetWithJung simWithJung = new SimternetWithJung();
		simWithJung.start();
	}

	SimternetWithJung() {
		this.init();
	}

	private void init() {
		// TODO: Auto-generated method stub

		// Initialize Simulation
		this.sim = new Simternet(System.currentTimeMillis());

		// Initialize GUI
		this.frame = new JFrame("Simternet -- Simulates the growth of the internet");

		this.dimension = new Dimension(600, 600);

		this.graph = new DirectedSparseGraph<Network, BackboneLink>();

		// Set up transformers to move the vertices to the right pixels
		CompositeLayoutTransformer transformer = new CompositeLayoutTransformer();
		transformer.addTransformer(new EdgeLayoutTransformer(new Dimension(100, 100)), 1, new Dimension(100,
				100));
		transformer.addTransformer(new BackboneNetworkToPixelTransformer(new Dimension(100, 500)), 1, new Dimension(
				400, 100));
		transformer.addTransformer(new DataCenterNetworkToPixelTransformer(new Dimension(100, 500)), 1, new Dimension(
				500, 100));
		transformer.addTransformer(new RandomLayoutTransformer(new Dimension(400, 400)), 10, new Dimension(200,
				200));

		this.layout = new StaticLayout<Network, BackboneLink>(this.graph, transformer);

		this.viewer = new VisualizationViewer<Network, BackboneLink>(this.layout, this.dimension);
		this.viewer.getModel().setGraphLayout(this.layout, this.dimension);
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

	private void start() {
		// start simulation
		// TODO: have simulation call update() if possible.
		this.sim.start();
		// for (int i = 0; i < 50; i++)
		// this.sim.schedule.step(this.sim);
		// System.out.println("Simulation Done!");
		// this.update();
	}

	public void update() {

		this.sim.schedule.step(this.sim);

		for (ApplicationServiceProvider asp : this.sim.getASPs(AppCategory.COMMUNICATION)) {
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

		this.viewer.repaint();
		this.frame.repaint();
	}
}