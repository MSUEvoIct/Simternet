package simternet.nsp;

import simternet.Simternet;

public interface NSPIndividual {

	void buildEdges(Simternet s, NSP nsp);

	void manageBackbone(Simternet s, NSP nsp);

	void priceEdges(Simternet s, NSP nsp);

	void priceBandwidth(Simternet s, NSP nsp);

}
