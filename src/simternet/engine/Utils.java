package simternet.engine;

public class Utils {
	public static String padLeft(String s, int n) {
		return String.format("%1$#" + n + "s", s);
	}
}
