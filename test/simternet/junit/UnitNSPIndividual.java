package simternet.junit;

import simternet.Simternet;
import simternet.nsp.NSP;
import simternet.nsp.NSPIndividual;
import ec.vector.FloatVectorIndividual;

public class UnitNSPIndividual extends FloatVectorIndividual implements NSPIndividual {
	private static final long serialVersionUID = 1L;

	@Override
	public void buildEdges(Simternet s, NSP nsp) {
		// Build an edge network everywhere
		for (byte x = 0; x < s.landscapeSizeX; x++) {
			for (byte y = 0; y < s.landscapeSizeY; y++) {
				if (nsp.edgeNetworks[x][y] == null)
					nsp.buildNetwork(x, y);
			}
		}
		
	}

	@Override
	public void manageBackbone(Simternet s, NSP nsp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void priceEdges(Simternet s, NSP nsp) {
		for (byte x = 0; x < s.landscapeSizeX; x++) {
			for (byte y = 0; y < s.landscapeSizeY; y++) {
				if (nsp.edgeNetworks[x][y] != null)
					nsp.edgeNetworks[x][y].price = 1;
			}
		}
	}

	
	@Override
	public void priceBandwidth(Simternet s, NSP nsp) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
