package sim.app.mytutorial1and2;

import sim.engine.*;
import sim.field.grid.*;

public class CA implements Steppable{
	//width and height will be updated later
	public IntGrid2D tempGrid = new IntGrid2D(0,0);

	public void step(SimState state){
		Tutorial1 tut = (Tutorial1)state;
		tempGrid.setTo(tut.grid); //Copy grid into tempGrid
		
		//For each cell...
		int width = tempGrid.getWidth();
		int height = tempGrid.getHeight();
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int count = 0;
				//count the number of live neighbors around the cell,
				//to simplify the for-loop include the cell itself as well
				for(int dx = -1; dx < 2; dx++){
                    for(int dy = -1; dy < 2; dy++){
                    	//stx and sty will fix out-of bounds arrays because we are using a
						//toridal grid, so in a 100x100 grid, if we pass -23, it will address 77.
						//NOTE: This only works if we are at most one dimension out of bounds (e.g. 123 is fine, 223 is not)
                    	//for a lazier but slower implementation use tx(x) and ty(y)
                        count += tempGrid.field[tempGrid.stx(x+dx)][tempGrid.sty(y+dy)]; 	
                    }
				}
                
                // since we're including the cell itself, the rule is slightly different:
                // if the count is 2 or less, or 5 or higher, the cell dies
                // else if the count is 3 exactly, a dead cell becomes live again
                // else the cell stays as it is
                        
                if (count <= 2 || count >= 5){  // death
                    tut.grid.field[x][y] = 0;
                } else if (count == 3){           // birth
                    tut.grid.field[x][y] = 1;
                }
			}

		}
	}
	
}
