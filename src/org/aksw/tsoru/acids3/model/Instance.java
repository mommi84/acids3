package org.aksw.tsoru.acids3.model;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.db.Tuple;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Instance extends GeneralNode {

	private ArrayList<Tuple> tuples;
	
	private boolean crawled = false;

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
				"URI",
				processing
		));	}

	public void setCrawled(boolean crawled) {
		this.crawled = crawled;
	}
	
	public boolean isCrawled() {
		return crawled;
	}
	
}
