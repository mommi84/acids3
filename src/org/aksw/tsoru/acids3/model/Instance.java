package org.aksw.tsoru.acids3.model;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.db.Tuple;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Instance {

	private String uri;

	private ArrayList<Tuple> tuples;

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
	
}
