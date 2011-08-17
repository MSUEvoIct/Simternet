package simternet.jung;

/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Mar 8, 2005
 *
 */

import java.awt.event.MouseEvent;

import simternet.gui.GUI;
import simternet.network.BackboneLink;
import simternet.network.Network;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;

/**
 * Calls GUI.vertexPicked(Network) when a single vertex is selected.
 * 
 * @author graysonwright
 */
/*
 * Slight abuse of standard object-oriented rules here... This class doesn't
 * actually have much to do with AnimatedPickingGraphMousePlugin, and so it
 * should not technically be a subclass. However, it was a LOT easier to write
 * it this way, because I only had to overwrite one method and the constructors.
 * 
 * Also, I had run into difficulty when I tried to write it on my own
 * 
 * TODO: Fix this Object-Oriented abuse case.
 */
public class VertexPickPlugin extends AnimatedPickingGraphMousePlugin<Network, BackboneLink> {

	protected GUI	owner;

	public VertexPickPlugin(GUI owner) {
		super();
		this.owner = owner;
	}

	/**
	 * create an instance, overriding the default modifiers
	 * 
	 * @param selectionModifiers
	 */
	public VertexPickPlugin(int selectionModifiers, GUI owner) {
		super(selectionModifiers);
		this.owner = owner;

	}

	/**
	 * If a Vertex was picked in the mousePressed event, notify the
	 * VisualizationViewer of the selection.
	 * 
	 * @param event
	 *            the MouseEvent
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		if (event.getModifiers() == this.modifiers)
			if (this.vertex != null)
				this.owner.vertexPicked(this.vertex);
	}
}