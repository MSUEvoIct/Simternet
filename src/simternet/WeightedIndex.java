/**
 * A tree-like weighted array.
 */

package simternet;


import java.util.ArrayList;



public class WeightedIndex<T> {

	protected ArrayList<T>		objects			= new ArrayList<T>();
	protected ArrayList<Double>	cum_weights		= new ArrayList<Double>();
	protected ArrayList<Double>	local_weight	= new ArrayList<Double>();



	public WeightedIndex() {
	}



	public Double totalWeight() {
		return (cum_weights.size() > 0) ? cum_weights.get(0) : 0.0;
	}



	public void add(T obj, Double weight) {
		objects.add(obj);
		local_weight.add(weight);
		cum_weights.add(weight);
		int ndx = objects.size() - 1;
		int parent_index = (ndx - 1) / 2;
		while (parent_index > 0) {
			double new_weight = cum_weights.get(parent_index) + weight;
			cum_weights.set(parent_index, new_weight);
			parent_index = (parent_index - 1) / 2;
		}
		if (ndx != 0)
			cum_weights.set(0, cum_weights.get(0) + weight);
	}



	public T get(double weight) throws IndexOutOfBoundsException {
		if (objects.size() == 0 || weight > cum_weights.get(0))
			throw new IndexOutOfBoundsException();
		return get(weight, 0);
	}



	protected T get(double weight, int cur) {
		double cur_weight = local_weight.get(cur);
		// System.err.println("weight=" + weight + ", ndx=" + cur +
		// "   cur_weight=" + cur_weight);
		double lhs_weight = (lhs(cur) < cum_weights.size()) ? lhs_weight(cur) : Double.NaN;
		double rhs_weight = (rhs(cur) < cum_weights.size()) ? rhs_weight(cur) : Double.NaN;
		// System.err.println("\t lhs_weight = " + (lhs_weight + cur_weight) +
		// "\t rhs_weight=" + (rhs_weight + cur_weight
		// + lhs_weight));
		if (weight < cur_weight) {
			// System.err.println("Return.");
			return objects.get(cur);
		} else if (weight >= (cur_weight + lhs_weight)) {
			// System.err.println("\t Taking right.");
			return get(weight - lhs_weight - cur_weight, rhs(cur));
		} else {
			// System.err.println("\t Taking left.");
			return get(weight - cur_weight, lhs(cur));
		}
	}



	protected double lhs_weight(int ndx) {
		return cum_weights.get(lhs(ndx));
	}



	protected double rhs_weight(int ndx) {
		return cum_weights.get(rhs(ndx));
	}



	protected int lhs(int ndx) {
		return 2 * ndx + 1;
	}



	protected int rhs(int ndx) {
		return 2 * (ndx + 1);
	}

}
