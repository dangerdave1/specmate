package com.specmate.bdd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.transformations.qmc.QuineMcCluskeyAlgorithm;
import static org.logicng.formulas.FType.*;

import com.specmate.model.base.IContentElement;
import com.specmate.model.base.IModelNode;
import com.specmate.model.bdd.*;
import com.specmate.model.requirements.CEGConnection;
import com.specmate.model.requirements.CEGModel;
import com.specmate.model.requirements.CEGNode;
import com.specmate.model.requirements.NodeType;
import com.specmate.model.requirements.RequirementsFactory;
import com.specmate.model.support.util.SpecmateEcoreUtil;
import com.specmate.common.AssertUtil;

public class BDD2CEGTranslator {

	// list of all nodes in the model
	private List<IModelNode> nodes;

	// bidirectional mapping between indices and (variable, condition) pairs
	private DoubleMap indexmap;

	// mapping from actual node to corresponding RecNode
	private Map<BDDNode, RecNode> act2rec;

	/*
	 * the method to be called from other classes
	 */
	public CEGModel translate(BDDModel model) {
		setup(model);
		createIndexMap();
		Formula f = translateToDNF();
		CEGModel ret = createCeg(f);
		return ret;
	}
	
	/*
	 * Put nodes into nodes list. Then creates a RecNode for each element in the
	 * nodes list and creates the mapping between them (in act2rec).
	 */
	private void setup(BDDModel model) {
		nodes = (List<IModelNode>) SpecmateEcoreUtil.pickInstancesOf(model.getContents(), IModelNode.class);
		act2rec = new HashMap<>();
		for (IModelNode node : nodes) {
			RecNode rec = new RecNode((BDDNode) node);
			act2rec.put((BDDNode) node, rec);
		}
	}
	
	/*
	 * The purpose of this method is to establish the mapping between
	 * (variable, condition) pairs and indices (stored in indexmap).
	 */
	private void createIndexMap() {
		// put all nodes without incoming connections into a set
		Set<IModelNode> starters = new HashSet<>();
		nodes.stream().filter(node -> (node.getIncomingConnections().isEmpty())).forEach(node -> {
			starters.add(node);
		});

		// make sure that there is only one BDD
		AssertUtil.assertTrue(starters.size() == 1);

		// set the start node
		IModelNode start = null;
		for (IModelNode node : starters) {
			start = node;
		}

		// this stops BDDs that consist of a terminal only
		AssertUtil.assertTrue(start instanceof BDDNoTerminalNode);

		// initialize a new indexmap each time this method is executed
		indexmap = new DoubleMap();

		// start recursion on the RecNode of start
		act2rec.get((BDDNode) start).setIndices(0, indexmap, act2rec);
	}

	/*
	 * Translates the BDD into a minimal disjunctive normal form (DNF). The BDD
	 * is traversed starting from the 1-terminals. The representation and
	 * minimization of the formula are handled by the LogicNG library.
	 */
	private Formula translateToDNF() {
		// put all 1-terminals into the ones set
		Set<IModelNode> ones = new HashSet<>();
		nodes.stream().filter(node -> (node.getOutgoingConnections().isEmpty())).forEach(node -> {
			//nonterminals without outgoing connections should be excluded
			if(node instanceof BDDTerminalNode){
				if (((BDDTerminalNode) node).isValue() == true) {
					ones.add(node);
				}
			}
		});

		// formula factory to create formulas
		FormulaFactory f = new FormulaFactory();

		// will contain a formula (conjunction) for each path
		Set<Formula> products = new HashSet<>();

		// start from each 1-terminal
		for (IModelNode node : ones) {
			// get the RecNode from act2rec
			RecNode current = act2rec.get((BDDNode) node);
			// first parameter: current path, which is initially just true
			current.findPaths(f.verum(), products, indexmap, act2rec);
		}

		// create the sum of all products
		Formula dnf = f.or(products);
		
		// return minimized DNF
		dnf = QuineMcCluskeyAlgorithm.compute(dnf);
		return dnf;
	}

	/*
	 * Creates a CEG from the given formula if possible. Decides whether the
	 * given formula is a LITERAL, an AND or an OR.
	 */
	private CEGModel createCeg(Formula formula) {
		switch (formula.type()) {
		case LITERAL:
			return literalCase(formula);
		case OR:
			return orCase(formula);
		// just one Minterm
		case AND:
			return andCase(formula);
		default:
			throw new IllegalArgumentException("ceg cannot be generated from this formula");
		}
	}
	
	/*
	 * Creates a CEG Model with an output node as the only element of the model.
	 * The returned model can be used for both the LITERAL and the OR case.
	 */
	private CEGModel createBasicCeg() {
		// create CEGModel
		CEGModel ceg = RequirementsFactory.eINSTANCE.createCEGModel();
		ceg.setId("Model-Translated-1");
		ceg.setName("Translated from BDD");

		// create output node
		CEGNode out = RequirementsFactory.eINSTANCE.createCEGNode();
		out.setId("node-out");
		out.setName("node-out");
		out.setVariable("BDD");
		out.setCondition("is true");
		out.setType(NodeType.OR);
		out.setX(675);
		out.setY(90);

		// add component to model
		ceg.getContents().add(out);
		return ceg;
	}
	
	/*
	 * Handles the LITERAL case of the CEG generation. In this case the final
	 * CEG consists of an input node, an output node and a connection between
	 * them. LITERAL case: formula that is only a literal, e.g. ~A.
	 */
	private CEGModel literalCase(Formula formula) {
		CEGModel ceg = createBasicCeg();

		// create the input node
		CEGNode in = RequirementsFactory.eINSTANCE.createCEGNode();
		int index = Integer.parseInt(((Literal) formula).name());
		in.setId("node-in-" + index);
		in.setName("node-in-" + index);
		// get (variable, condition) pair for the index
		Pair<String, String> vc = indexmap.getPairFor(index);
		in.setVariable(vc.getLeft());
		in.setCondition(vc.getRight());
		in.setX(45);
		in.setY(90);

		// create connection
		CEGConnection connection = RequirementsFactory.eINSTANCE.createCEGConnection();
		connection.setId("conn-1");
		connection.setName("conn-1");
		connection.setSource(in);
		// out node must be the first element as it is the only one
		connection.setTarget((CEGNode) ceg.getContents().get(0));
		if (((Literal) formula).phase() == false) {
			// negated literal (e.g. ~A) -> negated connection
			connection.setNegate(true);
		}

		// add components to model
		ceg.getContents().add(in);
		ceg.getContents().add(connection);
		return ceg;
	}
	
	/*
	 * Creates a CEG Model for a formula that is a disjunction, e.g. ~A | A & B.
	 */
	private CEGModel orCase(Formula formula) {
		CEGModel ceg = createBasicCeg();

		// mapping from variable name to node to make sure that there is only
		// one node for each variable
		Map<String, CEGNode> name2node = new HashMap<>();

		// current count of the connection; used for setting the connection ids
		int number_connections = 0;

		// current number of minterms; used for setting the node ids
		int number_minterms = 0;

		// current number of input nodes; used for setting their position
		int number_inputs = 0;

		// iterate through all minterms of the formula
		Iterator<Formula> or_it = formula.iterator();
		while (or_it.hasNext()) {
			Formula minterm = or_it.next();
			AssertUtil.assertTrue(minterm.type() == LITERAL || minterm.type() == AND);

			if (minterm.type() == LITERAL) {
				// this method puts the nodes and connections in the model
				number_inputs = number_inputs
						+ orCase_lit(ceg, (Literal) minterm, name2node, number_connections, number_inputs);
				// the method has added exactly one connection
				number_connections++;
			} else { // minterm.type()==AND
						// updating the variables
				Pair<Integer, Integer> pair = orCase_and(ceg, minterm, name2node, number_minterms, number_connections,
						number_inputs);
				number_connections = number_connections + pair.getLeft();
				number_inputs = number_inputs + pair.getRight();
				number_minterms++;
			}
		} // end of while

		return ceg;
	}
	
	/*
	 * Is called when a minterm is just a literal. In this case the node for
	 * this variable is directly connected to the output node (no extra minterm
	 * node).
	 */
	private int orCase_lit(CEGModel ceg, Literal l, Map<String, CEGNode> name2node, int number_connections,
			int number_inputs) {
		int ret = 0;

		// in node for this variable has not been created yet
		if (!name2node.containsKey(l.name())) {
			CEGNode node = RequirementsFactory.eINSTANCE.createCEGNode();
			int index = Integer.parseInt(l.name());
			node.setId("node-in-" + index);
			node.setName("node-in-" + index);
			Pair<String, String> pair = indexmap.getPairFor(index);
			node.setVariable(pair.getLeft());
			node.setCondition(pair.getRight());
			node.setX(45);
			node.setY(90 + (90 * number_inputs));
			ceg.getContents().add(node);
			// new node -> increase number_of_nodes
			name2node.put(l.name(), node);
			ret++;
		}

		// create connection from this in node to output
		CEGConnection connection = RequirementsFactory.eINSTANCE.createCEGConnection();
		connection.setId("conn-" + (number_connections + 1));
		connection.setName("conn-" + (number_connections + 1));
		// get the node for this variable from the map
		connection.setSource(name2node.get(l.name()));
		// the output node has been added first, so it should be in the first place
		connection.setTarget((CEGNode) ceg.getContents().get(0));
		if (l.phase() == false) {
			// negated literal (e.g. ~A) -> negated connection
			connection.setNegate(true);
		}

		ceg.getContents().add(connection);
		return ret;
	}
	
	private Pair<Integer, Integer> orCase_and(CEGModel ceg, Formula minterm, Map<String, CEGNode> name2node,
			int number_minterms, int number_connections, int number_inputs) {
		// tells the caller how many connections/input nodes have been created
		int add_conn = 0;
		int add_input = 0;

		// create a new node for this minterm
		CEGNode minnode = RequirementsFactory.eINSTANCE.createCEGNode();
		minnode.setId("node-min-" + (number_minterms + 1));
		minnode.setName("node-min-" + (number_minterms + 1));
		minnode.setVariable("Min" + (number_minterms + 1));
		minnode.setCondition("is true");
		minnode.setType(NodeType.AND);
		minnode.setX(375);
		minnode.setY(90 + (90 * number_minterms));
		ceg.getContents().add(minnode);

		// create connection from this minnode to output
		CEGConnection min2out = RequirementsFactory.eINSTANCE.createCEGConnection();
		min2out.setId("conn-" + (number_connections + 1));
		min2out.setName("conn-" + (number_connections + 1));
		add_conn++;
		min2out.setSource(minnode);
		// out node should be the first element
		min2out.setTarget((CEGNode) ceg.getContents().get(0));
		ceg.getContents().add(min2out);

		// iterate through all literals of the minterm
		Iterator<Formula> and_it = minterm.iterator();
		while (and_it.hasNext()) {
			Formula f = and_it.next();
			AssertUtil.assertTrue(f.type() == LITERAL);
			Literal l = (Literal) f;

			// in node for this variable has not been created yet
			if (!name2node.containsKey(l.name())) {
				CEGNode innode = RequirementsFactory.eINSTANCE.createCEGNode();
				int index = Integer.parseInt(l.name());
				innode.setId("node-in-" + index);
				innode.setName("node-in-" + index);
				Pair<String, String> pair = indexmap.getPairFor(index);
				innode.setVariable(pair.getLeft());
				innode.setCondition(pair.getRight());
				name2node.put(l.name(), innode);
				innode.setX(45);
				innode.setY(90 + (90 * (number_inputs + add_input)));
				add_input++;
				ceg.getContents().add(innode);
			}

			// create connection from this in node to minterm
			CEGConnection in2min = RequirementsFactory.eINSTANCE.createCEGConnection();
			add_conn++;
			in2min.setId("conn-" + (number_connections + add_conn));
			in2min.setName("conn-" + (number_connections + add_conn));
			in2min.setSource(name2node.get(l.name()));
			in2min.setTarget(minnode);
			if (l.phase() == false) {
				// negated literal (e.g. ~A) -> negated connection
				in2min.setNegate(true);
			}

			ceg.getContents().add(in2min);
		}

		return Pair.of(add_conn, add_input);
	}

	/*
	 * Handles the AND case of the CEG generation. In this case there is only
	 * one minterm. Instead of creating a node for the minterm the input nodes
	 * are connected directly to the output node. The output node is set to AND.
	 */
	private CEGModel andCase(Formula minterm) {
		// mapping from variable name to node to make sure that there is only
		// one node for each variable
		Map<String, CEGNode> name2node = new HashMap<>();

		// current number of input nodes; used for setting their position
		int number_inputs = 0;

		// current count of the connection; used for setting the connection ids
		int number_connections = 0;

		//get a basic CEG from general method
		CEGModel ceg = createBasicCeg();
		
		// changing the operator of the output node to AND
		IContentElement out = ceg.getContents().get(0);
		AssertUtil.assertTrue(out instanceof CEGNode);
		((CEGNode) out).setType(NodeType.AND);

		// iterate through all literals of the minterm
		Iterator<Formula> and_it = minterm.iterator();
		while (and_it.hasNext()) {
			Formula f = and_it.next();
			AssertUtil.assertTrue(f.type() == LITERAL);
			Literal l = (Literal) f;

			// in node for this variable has not been created yet
			if (!name2node.containsKey(l.name())) {
				CEGNode innode = RequirementsFactory.eINSTANCE.createCEGNode();
				int index = Integer.parseInt(l.name());
				System.out.println(index);
				innode.setId("node-in-" + index);
				innode.setName("node-in-" + index);
				Pair<String, String> pair = indexmap.getPairFor(index);
				innode.setVariable(pair.getLeft());
				innode.setCondition(pair.getRight());
				name2node.put(l.name(), innode);
				innode.setX(45);
				innode.setY(90 + (90 * number_inputs));
				ceg.getContents().add(innode);
				number_inputs++;
			}

			// create connection from this in node to outnode
			CEGConnection in2out = RequirementsFactory.eINSTANCE.createCEGConnection();
			number_connections++;
			in2out.setId("conn-" + number_connections);
			in2out.setName("conn-" + number_connections);
			in2out.setSource(name2node.get(l.name()));
			in2out.setTarget((CEGNode) out);
			if (l.phase() == false) {
				// negated literal (e.g. ~A) -> negated connection
				in2out.setNegate(true);
			}

			ceg.getContents().add(in2out);
		}
		return ceg;
	} //end of andCase
}
