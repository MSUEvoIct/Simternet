package simternet.ecj;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ec.EvolutionState;
import ec.gp.GPFunctionSet;
import ec.gp.GPInitializer;
import ec.gp.GPNode;
import ec.gp.GPType;
import ec.util.Parameter;

public class GPFunctionSetSet extends GPFunctionSet {

	public static final String	P_FUNCSET			= "funcSet";
	private static final long	serialVersionUID	= 1L;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// What's my name?
		this.name = state.parameters.getString(base.push(GPFunctionSet.P_NAME), null);
		if (this.name == null)
			state.output.fatal("No name was given for this function set.", base.push(GPFunctionSet.P_NAME));
		// Register me
		GPFunctionSet old_functionset = (GPFunctionSet) (((GPInitializer) state.initializer).functionSetRepository.put(
				this.name, this));
		if (old_functionset != null)
			state.output.fatal("The GPFunctionSet \"" + this.name + "\" has been defined multiple times.", base
					.push(GPFunctionSet.P_NAME));

		int numFuncSets = state.parameters.getInt(base.push(GPFunctionSet.P_SIZE), null, 1);
		Vector<String> funcSetBasesList = new Vector<String>();
		for (int i = 0; i < numFuncSets; i++) {
			Parameter p = base.push(GPFunctionSetSet.P_FUNCSET).push("" + i);
			String funcSetBase = state.parameters.getString(p, null);
			funcSetBasesList.add(funcSetBase);
		}

		this.nodesByName = new Hashtable();
		Vector tmp = new Vector();

		for (String funcSetBase : funcSetBasesList) {

			Parameter newBase = new Parameter(funcSetBase);

			// How many functions do I have?
			int numFuncs = state.parameters.getInt(newBase.push(GPFunctionSet.P_SIZE), null, 1);
			if (numFuncs < 1)
				state.output.error("The GPFunctionSet \"" + this.name + "\" has no functions.", newBase
						.push(GPFunctionSet.P_SIZE));

			Parameter p = newBase.push(GPFunctionSet.P_FUNC);
			for (int x = 0; x < numFuncs; x++) {
				// load
				Parameter pp = p.push("" + x);
				GPNode gpfi = (GPNode) (state.parameters.getInstanceForParameter(pp, null, GPNode.class));
				gpfi.setup(state, pp);

				// add to my collection
				tmp.addElement(gpfi);

				// Load into the nodesByName hashtable
				GPNode[] nodes = (GPNode[]) (this.nodesByName.get(gpfi.name()));
				if (nodes == null)
					this.nodesByName.put(gpfi.name(), new GPNode[] { gpfi });
				else {
					// O(n^2) but uncommon so what the heck.
					GPNode[] nodes2 = new GPNode[nodes.length + 1];
					System.arraycopy(nodes, 0, nodes2, 0, nodes.length);
					nodes2[nodes2.length - 1] = gpfi;
					this.nodesByName.put(gpfi.name(), nodes2);
				}
			}
		}

		// Make my hash tables
		this.nodes_h = new Hashtable();
		this.terminals_h = new Hashtable();
		this.nonterminals_h = new Hashtable();

		// Now set 'em up according to the types in GPType

		Enumeration e = ((GPInitializer) state.initializer).typeRepository.elements();
		GPInitializer initializer = ((GPInitializer) state.initializer);
		while (e.hasMoreElements()) {
			GPType typ = (GPType) (e.nextElement());

			// make vectors for the type.
			Vector nodes_v = new Vector();
			Vector terminals_v = new Vector();
			Vector nonterminals_v = new Vector();

			// add GPNodes as appropriate to each vector
			Enumeration v = tmp.elements();
			while (v.hasMoreElements()) {
				GPNode i = (GPNode) (v.nextElement());
				if (typ.compatibleWith(initializer, i.constraints(initializer).returntype)) {
					nodes_v.addElement(i);
					if (i.children.length == 0)
						terminals_v.addElement(i);
					else
						nonterminals_v.addElement(i);
				}
			}

			// turn nodes_h' vectors into arrays
			GPNode[] ii = new GPNode[nodes_v.size()];
			nodes_v.copyInto(ii);
			this.nodes_h.put(typ, ii);

			// turn terminals_h' vectors into arrays
			ii = new GPNode[terminals_v.size()];
			terminals_v.copyInto(ii);
			this.terminals_h.put(typ, ii);

			// turn nonterminals_h' vectors into arrays
			ii = new GPNode[nonterminals_v.size()];
			nonterminals_v.copyInto(ii);
			this.nonterminals_h.put(typ, ii);
		}

		// I don't check to see if the generation mechanism will be valid here
		// -- I check that in GPTreeConstraints, where I can do the weaker check
		// of going top-down through functions rather than making sure that
		// every
		// single function has a compatible argument function (an unneccessary
		// check)

		state.output.exitIfErrors(); // because I promised when I called
		// n.setup(...)

		// postprocess the function set
		this.postProcessFunctionSet();
	}

}
