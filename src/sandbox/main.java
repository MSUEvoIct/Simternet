package sandbox;

import java.util.ArrayList;

import javax.activation.UnsupportedDataTypeException;

import simternet.Temporal;

public class main {

	/**
	 * @param args
	 * @throws UnsupportedDataTypeException 
	 */
	public static void main(String[] args) throws UnsupportedDataTypeException {
		// TODO Auto-generated method stub
		Temporal<Double> price = new Temporal<Double>(5.0, 7.0);
		ArrayList<String> y = new ArrayList<String>();
		y.add("test");
		Temporal<ArrayList<String>> l = new Temporal<ArrayList<String>>(y, y);
		y = l.getOld();
		y.add("added");
		System.out.println(l.getOld());
		
	}

}
