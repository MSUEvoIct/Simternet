package simternet.ecj;

import ec.gp.GPData;

public class DoubleGP extends GPData {

	public double	value;

	@Override
	public void copyTo(GPData gpd) {
		((DoubleGP) gpd).value = this.value;
	}

}
