package simternet.network;

import sim.field.grid.SparseGrid2D;

public class Network2D extends SparseGrid2D{

	public Network2D(int width, int height){
        super(width, height);
	}
	
	public Network2D deepCopy(){
		Network2D ret = new Network2D(getHeight(), getWidth());
		for (Object o: getAllObjects()){
			
		}
		return ret;
	}
	
}
