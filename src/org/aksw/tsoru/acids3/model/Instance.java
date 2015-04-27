package org.aksw.tsoru.acids3.model;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.io.Processing;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Instance extends GeneralNode {

	private ArrayList<Tuple> tuples;
	
	private Processing processing;

	public Instance(String uri) {
		super(uri);
		this.tuples = new ArrayList<Tuple>();
	}

	public void setID(String id) {
		this.id = id;
	}
	
	public void add(Tuple t) {
		tuples.add(t);
	}
	
	public ArrayList<Tuple> getTuples() {
		return tuples;
	}
	
	public String toString() {
		return "<" + getID() + ">";
	}
	
	public void addInverse(Tuple t) {
		this.add(new Tuple(
				t.getO(),
				t.getP() + "_INV",
				t.getS(),
				"URI"
		));	}

	public Processing getProcessing() {
		return processing;
	}

	public void setProcessing(Processing processing) {
		this.processing = processing;
	}
	
}
