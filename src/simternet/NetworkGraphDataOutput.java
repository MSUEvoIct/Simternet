package simternet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import simternet.application.ApplicationServiceProvider;
import simternet.network.AbstractEdgeNetwork;
import simternet.network.AbstractNetwork;
import simternet.network.BackboneNetwork;
import simternet.network.Datacenter;
import simternet.nsp.AbstractNetworkProvider;

public class NetworkGraphDataOutput {

	private final String			connectionMatrixOutputFile	= "networkGraphConnectionMatrix.csv";
	private List<AbstractNetwork>	networks;
	private final Simternet			s;
	private final String			vertexDataOutputFile		= "networkGraphVertexData.tab";

	public NetworkGraphDataOutput(Simternet s, List<AbstractNetwork> networks) {
		this.s = s;
		this.networks = new ArrayList<AbstractNetwork>(networks);
	}

	private String getConnectionMatrix() {

		StringBuffer sb = new StringBuffer();

		for (AbstractNetwork n1 : this.networks) {
			boolean first = true;
			for (AbstractNetwork n2 : this.networks) {
				if (!first)
					sb.append(",");
				if (n1.isConnectedTo(n2))
					sb.append("1");
				first = false;
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	private String getVertexData() {
		StringBuffer sb = new StringBuffer();

		// Column Headers
		sb.append("Label");
		sb.append("\t");
		sb.append("Sides");
		sb.append("\t");
		sb.append("Color");
		sb.append("\t");
		sb.append("Size");
		sb.append("\n");

		for (AbstractNetwork an : this.networks) {
			// Label
			sb.append(an.toString() + "\t");

			// Default Values
			double size = 1.0;
			int sides = 3;
			String color = "black";

			if (an instanceof BackboneNetwork) {
				// Sides
				sides = 5; // Backbones are pentagons

				// Color
				color = "blue";

				// Size
				BackboneNetwork bb = (BackboneNetwork) an;
				AbstractNetworkProvider nsp = bb.getOwner();
				double customers = nsp.getCustomers();
				size = Math.log(customers);
			} else if (an instanceof AbstractEdgeNetwork) {
				// Sides
				sides = 3; // Edges others triangles

				// Color
				color = "red";

				// Size
				AbstractEdgeNetwork aen = (AbstractEdgeNetwork) an;
				size = Math.log(aen.getNumSubscribers());
			} else if (an instanceof Datacenter) {
				// Sides
				sides = 4; // Datacenters are squares

				// Color
				color = "green";

				// Size
				Datacenter d = (Datacenter) an;
				ApplicationServiceProvider asp = d.getOwner();
				double customers = asp.getCustomers();
				size = Math.log(customers);
			}

			sb.append(sides);
			sb.append("\t");
			sb.append(color);
			sb.append("\t");
			sb.append(size / 3);
			sb.append("\n");

		}
		return sb.toString();
	}

	public void output() {

		String outputDir = this.s.getParameters().getProperty("output.dir", "./data/output/");

		try {
			FileOutputStream connectionMatrixFile = new FileOutputStream(outputDir + this.connectionMatrixOutputFile);
			FileOutputStream vertexDataFile = new FileOutputStream(outputDir + this.vertexDataOutputFile);

			Writer connectionMatrixWriter = new OutputStreamWriter(connectionMatrixFile);
			Writer vertexDataWriter = new OutputStreamWriter(vertexDataFile);

			connectionMatrixWriter.write(this.getConnectionMatrix());
			vertexDataWriter.write(this.getVertexData());

			connectionMatrixWriter.flush();
			connectionMatrixWriter.close();

			vertexDataWriter.flush();
			vertexDataWriter.close();

			connectionMatrixFile.close();
			vertexDataFile.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
