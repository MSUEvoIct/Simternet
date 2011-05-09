package simternet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import simternet.application.ApplicationProvider;
import simternet.network.Backbone;
import simternet.network.Datacenter;
import simternet.network.EdgeNetwork;
import simternet.network.Network;
import simternet.nsp.NetworkProvider;

/**
 * Exports data on connections between networks suitable for importation into R.
 * 
 * @author kkoning
 * 
 */
public class NetworkGraphDataOutput {

	private final String	connectionMatrixOutputFile	= "networkGraphConnectionMatrix.csv";
	private List<Network>	networks;
	private final Simternet	s;
	private final String	vertexDataOutputFile		= "networkGraphVertexData.tab";

	public NetworkGraphDataOutput(Simternet s, List<Network> networks) {
		this.s = s;
		this.networks = new ArrayList<Network>(networks);
	}

	private String getConnectionMatrix() {

		StringBuffer sb = new StringBuffer();

		for (Network n1 : this.networks) {
			boolean first = true;
			for (Network n2 : this.networks) {
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

		for (Network an : this.networks) {
			// Label
			sb.append(an.toString() + "\t");

			// Default Values
			double size = 1.0;
			int sides = 3;
			String color = "black";

			if (an instanceof Backbone) {
				// Sides
				sides = 5; // Backbones are pentagons

				// Color
				color = "blue";

				// Size
				Backbone bb = (Backbone) an;
				NetworkProvider nsp = bb.getOwner();
				double customers = nsp.getCustomers();
				size = Math.log(customers);
			} else if (an instanceof EdgeNetwork) {
				// Sides
				sides = 3; // Edges others triangles

				// Color
				color = "red";

				// Size
				EdgeNetwork aen = (EdgeNetwork) an;
				size = Math.log(aen.getNumSubscribers());
			} else if (an instanceof Datacenter) {
				// Sides
				sides = 4; // Datacenters are squares

				// Color
				color = "green";

				// Size
				Datacenter d = (Datacenter) an;
				ApplicationProvider asp = d.getOwner();
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
