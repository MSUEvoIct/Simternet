package simternet.ecj;

import ec.gp.GPData;

public class BooleanGP extends GPData {

	public boolean	value;

	@Override
	public void copyTo(GPData gpd) {
		((BooleanGP) gpd).value = this.value;
	}

}
