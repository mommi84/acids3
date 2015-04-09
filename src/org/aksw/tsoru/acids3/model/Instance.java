package org.aksw.tsoru.acids3.model;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Triple;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Instance {

	private String uri;

	private ArrayList<Triple> triples;

	public Instance(String uri) {
		super();
		this.uri = uri;
		this.triples = new ArrayList<Triple>();
	}

	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public void addTriple(Triple t) {
		triples.add(t);
	}
	
	public ArrayList<Triple> getTriples() {
		return triples;
	}
	
	public String toString() {
		return "<" + getURI() + ">";
	}
	
}
