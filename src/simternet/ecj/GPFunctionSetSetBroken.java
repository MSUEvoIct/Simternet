package simternet.ecj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import ec.EvolutionState;
import ec.gp.GPFunctionSet;
import ec.gp.GPInitializer;
import ec.gp.GPNode;
import ec.util.Parameter;

/**
 * ECJ requires that each function set be defined individually. That will be
 * quite awkward for us, because we'll have many sets with many members. So
 * we're going to write our own GPFunctionSet that includes the union of GPNodes
 * present in other listed sets. This lets us to list, say, boolean nodes,
 * AbstractNetwork nodes, math nodes, etc... in arbitrary combinations...
 * building large collections of allowable GPNodes that way rather than listing
 * them all over and over again individually.
 * 
 * Unfortunately, GPFunctionSet wasn't written in a OO way, we we need to
 * replicate code, stick things into data structures that are accessed directly,
 * etc... A fair amount of code was pulled directle from GPFunctionSet and
 * repurposed here.
 * 
 * ABANDON ALL HOPE, YE WHO ENTER HERE: Something about this screws up the
 * random number generation- the random number generator is called a different
 * number of times based on something other than the random number generator.
 * 
 * @author kkoning
 * 
 */
@Deprecated
public class GPFunctionSetSetBroken extends GPFunctionSet {

	private static final String	P_FUNCSET			= "funcSet";
	private static final String	P_NUMSETS			= "size";
	private static final long	serialVersionUID	= 1L;
	Map<Object, Set<GPNode>>	nodes_by_name_tmp	= new HashMap<Object, Set<GPNode>>();
	Map<Object, Set<GPNode>>	nodes_h_tmp			= new HashMap<Object, Set<GPNode>>();
	Map<Object, Set<GPNode>>	nonterminals_h_tmp	= new HashMap<Object, Set<GPNode>>();
	Map<Object, Set<GPNode>>	terminals_h_tmp		= new HashMap<Object, Set<GPNode>>();

	private void absorbGPFunctionSet(GPFunctionSet fs) {
		this.absorbNodes(this.nodes_by_name_tmp, fs.nodesByName);
		this.absorbNodes(this.nodes_h_tmp, fs.nodes_h);
		this.absorbNodes(this.nonterminals_h_tmp, fs.nonterminals_h);
		this.absorbNodes(this.terminals_h_tmp, fs.terminals_h);
	}

	@SuppressWarnings("unchecked")
	private void absorbNodes(Map<Object, Set<GPNode>> target, Hashtable from) {

		for (Object key : from.keySet()) {
			GPNode[] nodeArray; // the array from the child classes
			Set<GPNode> nodeSet; // new sets, to combine and avoid duplication

			// get matching node set, create if doesn't exist
			if (!target.containsKey(key)) {
				nodeSet = new HashSet<GPNode>();
				target.put(key, nodeSet);
			} else
				nodeSet = target.get(key);

			nodeArray = (GPNode[]) from.get(key);

			for (GPNode node : nodeArray)
				nodeSet.add(node);
		}
	}

	@SuppressWarnings("unchecked")
	private void convertToArrays(Hashtable target, Map<Object, Set<GPNode>> temp) {
		for (Object key : temp.keySet()) {
			Set<GPNode> valueSet = temp.get(key);
			GPNode[] valueArray = valueSet.toArray(new GPNode[0]);
			target.put(key, valueArray);
		}
	}

	/*
	 * Instead of listing GPNodes individually, we're going to list other
	 * function sets, to make combining large lists easier.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see ec.gp.GPFunctionSet#setup(ec.EvolutionState, ec.util.Parameter)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setup(EvolutionState state, Parameter base) {

		int numFunctionSets = 0;

		this.name = state.parameters.getString(base.push(GPFunctionSet.P_NAME), null);
		if (this.name == null)
			state.output.fatal("No name was given for this function set.", base.push(GPFunctionSet.P_NAME));

		// Register me
		GPFunctionSet old_functionset = (GPFunctionSet) (((GPInitializer) state.initializer).functionSetRepository.put(
				this.name, this));

		// make sure we're not redefining the same set
		if (old_functionset != null)
			state.output.fatal("The GPFunctionSet \"" + this.name + "\" has been defined multiple times.", base
					.push(GPFunctionSet.P_NAME));

		// how many function sets do i contain?
		numFunctionSets = state.parameters.getInt(base.push(GPFunctionSet.P_SIZE), null, 1);
		if (numFunctionSets < 1)
			state.output.error("The GPFunctionSet \"" + this.name + "\" has no functions.", base
					.push(GPFunctionSet.P_SIZE));

		Parameter p = base.push(GPFunctionSetSetBroken.P_FUNCSET);

		// Absorb all the child function sets
		for (int x = 0; x < numFunctionSets; x++) {
			Parameter p_funcSet = p.push("" + x);
			String funcSetName = state.parameters.getString(p_funcSet, null);
			GPFunctionSet funcSet = GPFunctionSet.functionSetFor(funcSetName, state);

			this.absorbGPFunctionSet(funcSet);
		}

		this.nodesByName = new Hashtable();
		this.nodes_h = new Hashtable();
		this.nonterminals_h = new Hashtable();
		this.terminals_h = new Hashtable();

		// Put into the array format this class expects
		this.convertToArrays(this.nodesByName, this.nodes_by_name_tmp);
		this.convertToArrays(this.nodes_h, this.nodes_h_tmp);
		this.convertToArrays(this.nonterminals_h, this.nonterminals_h_tmp);
		this.convertToArrays(this.terminals_h, this.terminals_h_tmp);

		// Rely on parent class's post processing function
		super.postProcessFunctionSet();
	}

}
