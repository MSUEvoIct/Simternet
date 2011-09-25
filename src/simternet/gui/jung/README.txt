package: simternet.jung
author:  Grayson Wright

The simternet.jung package defines the appearance of a Simternet network when 
it is loaded from a checkpoint and displayed through the GUI. JUNG is the 
Java Universal Network/Graph Framework, which is used for presenting networks 
in a graphical interface.

In this package, LocationTransformer lays out the network elements onscreen. 

ConsumerNetwork is an adapter class that represents the consumers at a given 
location coordinate. This was introduced because originally, Edge Networks 
were represented in the graph. When they were drawn, three or four would 
overlap at any given location, which made it difficult to pull useful 
information from them. So now, at each location in the grid, there is only one 
network object.

VertexPickPlugin is a utility that notifies us when the user selects a vertex 
to see more information about it. It shouldn't need much maintenance.

simternet.jung.appearance defines other aspects of the network's visualization, 
including the shape, size and color of the vertices, the color, thickness, 
and style of the edges, and labels that appear onscreen.