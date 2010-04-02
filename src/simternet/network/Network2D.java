package simternet.network;

import java.util.Iterator;

import sim.field.grid.SparseGrid2D;

public class Network2D extends SparseGrid2D{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7260152817905845782L;

	public Network2D(int width, int height){
        super(width, height);
	}
	
	public Network2D deepCopy(){
		Network2D ret = new Network2D(getHeight(), getWidth());
		for (Iterator<AbstractNetwork> i = getAllObjects().iterator(); i.hasNext();){
			ret.setObjectLocation(i.next().deepCopy(), i.next().locationX, i.next().locationY);
		}
		return ret;
	}
	
}
