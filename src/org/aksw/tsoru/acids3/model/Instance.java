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
public class Instance {

	private String uri;

	private ArrayList<Tuple> tuples;
	
	private Processing processing;

	public Instance(String uri) {
		super();
		this.uri = uri;
		this.tuples = new ArrayList<Tuple>();
	}

	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public void addTuple(Tuple t) {
		tuples.add(t);
	}
	
	public ArrayList<Tuple> getTuples() {
		return tuples;
	}
	
	public String toString() {
		return "<" + getURI() + ">";
	}
	
	public void addTriple(Triple triple) {
		Node object = triple.getObject();
		String o = null, otype = null;
		if(object.isURI()) { 
			o = object.getURI();
			otype = "URI";
		}
		if(object.isLiteral()) {
			o = "" + object.getLiteral().getValue();
			otype = object.getLiteralDatatypeURI();
			if(otype == null)
				otype = "STRING";
		}
		tuples.add(new Tuple(triple.getSubject().getURI(),
				triple.getPredicate().getURI(),
				o, otype
		));
	}

	public void addInverseTriple(Triple triple) {
		this.addTuple(new Tuple(
				triple.getObject().getURI(),
				triple.getPredicate().getURI() + "_INV",
				triple.getSubject().getURI(),
				"URI"
		));
	}
	
	public void addInverseTuple(Tuple t) {
		this.addTuple(new Tuple(
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
