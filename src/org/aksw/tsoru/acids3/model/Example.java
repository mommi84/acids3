package org.aksw.tsoru.acids3.model;

import java.util.TreeSet;

import com.hp.hpl.jena.graph.Triple;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Example {

	private String uri;

	private TreeSet<Triple> triples;

	public Example(String uri) {
		super();
		this.uri = uri;
		this.triples = new TreeSet<Triple>();
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
	
	public TreeSet<Triple> getTriples() {
		return triples;
	}
	
}
