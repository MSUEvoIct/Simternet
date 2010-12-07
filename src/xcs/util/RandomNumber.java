package xcs.util;

import java.util.Random;

public class RandomNumber {

	public static double getDouble() {
		double assets;
		Random r = new Random(System.currentTimeMillis());
		do
			assets = r.nextDouble();
		while (assets < 0);
		return assets;
	}

	public RandomNumber() {

	}
}
